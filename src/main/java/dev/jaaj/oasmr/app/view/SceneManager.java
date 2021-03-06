/*
 * Copyright (c) 2019. JaaJ-dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.jaaj.oasmr.app.view;

import dev.jaaj.oasmr.app.view.exception.ExceptionSceneAlrdeadyExists;
import dev.jaaj.oasmr.app.Main;
import dev.jaaj.oasmr.app.view.exception.ExceptionSceneNotFound;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SceneManager {

    private final HashSet<View> views = new HashSet<>();
    private View activeView;
    private final Stage primaryStage;
    private static final SceneManager ourInstance = new SceneManager();
    ScheduledExecutorService updater = Executors.newSingleThreadScheduledExecutor();

    private SceneManager() {
        primaryStage = new Stage();
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setTitle("OASMR");
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("img/OASMR.png")));
        updater.scheduleAtFixedRate(() -> Platform.runLater(() -> activeView.update()), 5, 5, TimeUnit.SECONDS);
    }

    public static SceneManager getInstance() {
        return ourInstance;
    }

    public void addScene(View view) throws ExceptionSceneAlrdeadyExists {
        if (views.add(view)) {
            view.onCreate();
        } else {
            throw new ExceptionSceneAlrdeadyExists();
        }
    }

    public View getView(Class<? extends View> klazz) throws ExceptionSceneNotFound {
        for (View v : views) {
            if (v.getClass().equals(klazz)) {
                return v;
            }
        }
        throw new ExceptionSceneNotFound();

    }

    public Scene getScene(Class<? extends View> klazz) throws ExceptionSceneNotFound {
        for (View v : views) {
            if (v.getClass().equals(klazz)) {
                return v.getScene();
            }
        }
        throw new ExceptionSceneNotFound();

    }

    public void setScenes(Class<? extends View> klazz, int width, int height) throws ExceptionSceneNotFound {
        if (activeView != null)
            activeView.stop();
        View v = getView(klazz);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        v.start();
        activeView = v;
        primaryStage.setScene(v.getScene());
    }

    public void show() {
        primaryStage.show();
    }

}
