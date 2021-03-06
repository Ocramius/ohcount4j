/*
 * Copyright 2016 Black Duck Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blackducksoftware.ohcount4j.scan;

import java.io.IOException;

import com.blackducksoftware.ohcount4j.Language;
import com.blackducksoftware.ohcount4j.SourceFile;

public class BinaryScanner implements Scanner {

    private Language defaultLanguage = Language.BINARY;

    @Override
    public void setDefaultLanguage(Language language) {
        defaultLanguage = language;
    }

    @Override
    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    // Scan methods do nothing for binary files

    @Override
    public void scan(SourceFile blob, LineHandler handler) throws IOException {
    }

    @Override
    public void scan(char[] text, LineHandler handler) {
    }

    @Override
    public void scan(String text, LineHandler handler) {
    }

}
