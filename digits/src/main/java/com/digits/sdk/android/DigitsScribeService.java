/*
 * Copyright (C) 2015 Twitter, Inc.
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
 *
 */

package com.digits.sdk.android;

/**
 * Interface that represents all the scribe events of a single screen.
 */
interface DigitsScribeService {
    /**
     * scribe event when the screen is rendered
     */
    void impression();

    /**
     * scribe event when the use case of the screen has failed
     */
    void failure();

    /**
     * scribe event when a screen component is clicked
     */
    void click(DigitsScribeConstants.Element element);

    /**
     * scribe event when the use case of the screen has succeeded
     */
    void success();

    /**
     * scribe event for any API exception
     */
    void error(DigitsException exception);
}
