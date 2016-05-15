/*
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2015/1/21  Created
 */
package com.cwjcsu.common.util;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author atlas
 */
public class _ {
    public static String $(final String messagePattern,
                           final Object... argArray) {
        if (argArray == null || argArray.length == 0)
            return messagePattern;
        return $$(messagePattern, argArray);
    }

    public static String $$(final String messagePattern, final Object[] argArray) {
        return MessageFormatter.arrayFormat(messagePattern, argArray)
                .getMessage();
    }
}
