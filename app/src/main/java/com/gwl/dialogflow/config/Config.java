/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
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

package com.gwl.dialogflow.config;

import com.gwl.dialogflow.config.LanguageConfig;

public abstract class Config {
    // copy this keys from your developer dashboard
    public static final String ACCESS_TOKEN = "d5ca7be784a2488d817f500eb2411def";// client access token for Goodwork solar Dialogflow app

    public static final LanguageConfig[] languages = new LanguageConfig[]{
            new LanguageConfig("en", "d5ca7be784a2488d817f500eb2411def"),
            new LanguageConfig("ru", "d5ca7be784a2488d817f500eb2411def"),
            new LanguageConfig("de", "d5ca7be784a2488d817f500eb2411def"),
            new LanguageConfig("pt", "d5ca7be784a2488d817f500eb2411def"),
            new LanguageConfig("pt-BR", "d5ca7be784a2488d817f500eb2411def"),
            new LanguageConfig("es", "d5ca7be784a2488d817f500eb2411def"),
            new LanguageConfig("fr", "d5ca7be784a2488d817f500eb2411def"),
            new LanguageConfig("it", "d5ca7be784a2488d817f500eb2411def"),
            new LanguageConfig("ja", "d5ca7be784a2488d817f500eb2411def"),
            new LanguageConfig("ko", "d5ca7be784a2488d817f500eb2411def"),
            new LanguageConfig("zh-CN", "d5ca7be784a2488d817f500eb2411def"),
            new LanguageConfig("zh-HK", "d5ca7be784a2488d817f500eb2411def"),
            new LanguageConfig("zh-TW", "9cadea114425436cbaeaa504ea56555b"),
    };

    public static final String[] events = new String[]{
            "hello_event",
            "goodbye_event",
            "how_are_you_event"
    };
}
