/*
 * Copyright (c) 2010-2013 Evolveum
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

package com.evolveum.midpoint.web.resource.img;

import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * @author lazyman
 */
public class ImgResources {

    public static final String BASE_PATH = "/img";

    public static final String USER_PRISM = "user_prism.png";
    public static final String DECISION_PRISM = "decision_prism.png";
    public static final String ROLE_PRISM = "role_prism.png";
    public static final String TRACKING_PRISM = "tracking_prism.png";
    public static final String HDD_PRISM = "hdd_prism.png";
    public static final String USER_SUIT = "user_suit.png";
    public static final String DRIVE = "drive.png";

    public static final String BUILDING = "building.png";
    public static final String MEDAL_GOLD_3 = "medal_gold_3.png";
    public static final String MEDAL_SILVER_2 = "medal_silver_2.png";
    public static final String ERROR = "error.png";

    public static final String TOOLTIP_INFO = "tooltip_info.png";

    public static final String DELETED_VALUE = "DeletedValue.png";
    public static final String PRIMARY_VALUE = "PrimaryValue.png";
    public static final String SECONDARY_VALUE = "SecondaryValue.png";

    public static final String SHIELD = "shield.png";

    public static final String AJAX_LOADER = "ajax-loader.gif";

    public static PackageResourceReference createReference(String value) {
        return new PackageResourceReference(ImgResources.class, value);
    }
}
