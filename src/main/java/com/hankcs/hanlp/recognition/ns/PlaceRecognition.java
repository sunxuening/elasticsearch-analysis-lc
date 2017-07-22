/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/11/17 19:34</create-date>
 *
 * <copyright file="PlaceRecognition.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.liNSunsoft.com/
 * This source is subject to the LiNSunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.hankcs.hanlp.recognition.ns;

import com.hankcs.hanlp.algorithm.Viterbi;
import com.hankcs.hanlp.corpus.dictionary.item.EnumItem;
import com.hankcs.hanlp.corpus.tag.NS;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.ns.PlaceDictionary;
import com.hankcs.hanlp.seg.common.Vertex;
import com.hankcs.hanlp.seg.common.WordNet;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * 地址识别
 *
 * @author hankcs
 */
public class PlaceRecognition {
    public static boolean Recognition(List<Vertex> pWordSegResult, WordNet wordNetOptimum, WordNet wordNetAll) {
        List<EnumItem<NS>> roleTagList = roleTag(pWordSegResult, wordNetAll);
        List<NS> NSList = viterbiExCompute(roleTagList);
        PlaceDictionary.parsePattern(NSList, pWordSegResult, wordNetOptimum, wordNetAll);
        return true;
    }

    public static List<EnumItem<NS>> roleTag(List<Vertex> vertexList, WordNet wordNetAll) {
        List<EnumItem<NS>> tagList = new LinkedList<EnumItem<NS>>();
        ListIterator<Vertex> listIterator = vertexList.listIterator();

        while (listIterator.hasNext()) {
            Vertex vertex = listIterator.next();
            if (Nature.ns == vertex.getNature() && vertex.getAttribute().totalFrequency <= 1000) {
                if (vertex.realWord.length() < 3)               // 二字地名，认为其可以再接一个后缀或前缀
                    tagList.add(new EnumItem<NS>(NS.H, NS.G));
                else
                    tagList.add(new EnumItem<NS>(NS.G));        // 否则只可以再加后缀
                continue;
            }
            EnumItem<NS> NSEnumItem = PlaceDictionary.dictionary.get(vertex.word);  // 此处用等效词，更加精准
            if (NSEnumItem == null) {
                NSEnumItem = new EnumItem<NS>(NS.Z, PlaceDictionary.transformMatrixDictionary.getTotalFrequency(NS.Z));
            }
            tagList.add(NSEnumItem);
        }
        return tagList;
    }

    private static void insert(ListIterator<Vertex> listIterator, List<EnumItem<NS>> tagList, WordNet wordNetAll, int line, NS ns) {
        Vertex vertex = wordNetAll.getFirst(line);
        assert vertex != null : "全词网居然有空白行！";
        listIterator.add(vertex);
        tagList.add(new EnumItem<NS>(ns, 1000));
    }

    /**
     * 维特比算法求解最优标签
     *
     * @param roleTagList
     * @return
     */
    public static List<NS> viterbiExCompute(List<EnumItem<NS>> roleTagList) {
        return Viterbi.computeEnum(roleTagList, PlaceDictionary.transformMatrixDictionary);
    }
}
