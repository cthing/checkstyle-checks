/*
 * Copyright 2022 C Thing Software
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
package checkstyle;


public class LogDeclaration {
    private static final Logger LOG = LoggerFactory.getLogger(LogDeclaration.class);
}

class AnnotatedLogDeclaration {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedLogDeclaration.class);
}

class BadModifiers {
    private Logger LOG = LoggerFactory.getLogger(BadModifiers.class);
}

class BadFactory {
    private static final Logger LOG = Logger.getLogger(BadFactory.class);
}

class MissingAssign {
    private static final Logger LOG;
}

class WrongClass {
    private static final Logger LOG = LoggerFactory.getLogger(LogDeclaration.class);
}

class MissingClass {
    private static final Logger LOG = LoggerFactory.getLogger(MissingClass.getClass());
}

class BadVariableName {
    private static final Logger log = LoggerFactory.getLogger(BadVariableName.class);
}

class SyntaxError1 {
    private static final Logger LOG = LoggerFactory(SyntaxError1.class);
}
