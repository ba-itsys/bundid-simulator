/*
 * Copyright 2025. IT-Systemhaus der Bundesagentur fuer Arbeit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.ba.oiam.bundidsim;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * SpringBoot Main class
 */
@SpringBootApplication
@Slf4j
public class MainApplication {

    private final String appName;
    private final String appVersion;

    static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(MainApplication.class)
                .run(args);
    }

    public MainApplication(@Value("${spring.application.name}") String appName,
                           @Value("${spring.application.version}") String appVersion) {
        this.appName = appName;
        this.appVersion = appVersion;
    }

    /**
     * Log der Version nach dem Start der Application.
     */
    @EventListener(ApplicationReadyEvent.class)
    private void test() {
        log.info("#### Start Application: [{}], Version: [{}] gestartet.", appName, appVersion);
    }
}
