/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/9/15 19:39</create-date>
 *
 * <copyright file="CoreStopwordDictionary.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.hankcs.hanlp.dictionary.stopword;

import com.hankcs.hanlp.api.HanLpGlobalSettings;
import com.hankcs.hanlp.log.HanLpLogger;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;
import java.util.ListIterator;


/**
 * 核心停用词词典
 *
 * @author hankcs
 */
public class CoreStopWordDictionary {
    static StopWordDictionary dictionary;

    static {
        try {
            dictionary = new StopWordDictionary(HanLpGlobalSettings.CoreStopWordDictionaryPath);
        }
        catch (Exception e) {
            HanLpLogger.error(CoreStopWordDictionary.class,
                    String.format("Failed to load dictionary[CoreStopWordDictionary], path[%s]",
                            HanLpGlobalSettings.CoreStopWordDictionaryPath));
        }
    }

    public static boolean contains(String key) {
        return dictionary.contains(key);
    }

    /**
     * 核心停用词典的核心过滤器，词性属于名词、动词、副词、形容词，并且不在停用词表中才不会被过滤
     */
    public static Filter FILTER = new Filter() {
        @Override
        public boolean shouldInclude(Term term) {
            // 除掉停用词
            String nature = term.nature != null ? term.nature.toString() : "空";
            char firstChar = nature.charAt(0);
            switch (firstChar) {
                case 'm':
                case 'b':
                case 'c':
                case 'e':
                case 'o':
                case 'p':
                case 'q':
                case 'u':
                case 'y':
                case 'z':
                case 'r':
                case 'w': {
                    return false;
                }
                default: {
                    if (!CoreStopWordDictionary.contains(term.word)) {
                        return true;
                    }
                }
                break;
            }

            return false;
        }
    };

    /**
     * 是否应当将这个term纳入计算
     *
     * @param term
     * @return 是否应当
     */
    public static boolean shouldInclude(Term term) {
        return FILTER.shouldInclude(term);
    }

    /**
     * 是否应当去掉这个词
     *
     * @param term 词
     * @return 是否应当去掉
     */
    public static boolean shouldRemove(Term term) {
        return !shouldInclude(term);
    }

    /**
     * 加入停用词到停用词词典中
     *
     * @param stopWord 停用词
     * @return 词典是否发生了改变
     */
    public static boolean add(String stopWord) {
        return dictionary.add(stopWord);
    }

    /**
     * 从停用词词典中删除停用词
     *
     * @param stopWord 停用词
     * @return 词典是否发生了改变
     */
    public static boolean remove(String stopWord) {
        return dictionary.remove(stopWord);
    }

    /**
     * 对分词结果应用过滤
     *
     * @param termList
     */
    public static void apply(List<Term> termList) {
        ListIterator<Term> listIterator = termList.listIterator();
        while (listIterator.hasNext()) {
            if (shouldRemove(listIterator.next())) listIterator.remove();
        }
    }
}
