package at.cleancode.dynamicproperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class FileDynamicPropertySource implements ApplicationListener<ContextRefreshedEvent> {

    private final File file;
    private DynamicPropertiesManager manager;

    public FileDynamicPropertySource(File file) {
        this.file = Assert.argumentNotNull(file, "file");
    }

    private void fileChanged() throws IOException {
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(file)) {
            properties.load(is);
            if (properties.isEmpty()) {
                return;
            }
            Map<String, String> map = new HashMap<>();
            for (String name : properties.stringPropertyNames()) {
                map.put(name, properties.getProperty(name));
            }
            manager.propertiesChanged(map);
        } catch (FileNotFoundException e) {
            // file gone, ignore
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Assert.argumentNotNull(contextRefreshedEvent, "contextRefreshedEvent");
        manager = contextRefreshedEvent.getApplicationContext().getBean(DynamicPropertiesManager.class);
        readFileOnStartup();
        createBackgroundThread();
    }

    private void readFileOnStartup() {
        try {
            fileChanged();
        } catch (IOException e) {
            throw new DynamicPropertiesException("Could not read properties file", e);
        }
    }

    private void createBackgroundThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    WatchService watchService = FileSystems.getDefault().newWatchService();
                    Path path = file.getAbsoluteFile().getParentFile().toPath();
                    path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

                    while (true) {
                        try {
                            WatchKey wk = watchService.take();
                            boolean changed = false;
                            for (WatchEvent<?> event : wk.pollEvents()) {
                                Path context = (Path) event.context();
                                if (context.getFileName().toString().equals(file.getName())) {
                                    changed = true;
                                }
                            }
                            wk.reset();
                            if (changed) {
                                fileChanged();
                            }
                        } catch (NoSuchFileException e) {
                            // file gone, ignore
                        } catch (IOException e) {
                            // something went wrong, but it's best to continue with the while loop to watch for further
                            // changes.
                        } catch (InterruptedException e) {
                            Thread.interrupted();
                        }
                    }
                } catch (NoSuchFileException e) {
                    // file gone, ignore
                } catch (IOException e) {
                    throw new DynamicPropertiesException("Error processing properties file", e);
                }
            }
        };
        thread.setName(getClass().getSimpleName() + " Watcher Thread");
        thread.setDaemon(true);
        thread.start();
    }

}
