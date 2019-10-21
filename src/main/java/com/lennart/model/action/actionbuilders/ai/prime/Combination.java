package com.lennart.model.action.actionbuilders.ai.prime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by LennartMac on 14/10/2019.
 */
public class Combination {

    public List<String> getAllCombinationsOfList(List<String> initialList) {
        List<String> comboList = new ArrayList<>();

        String[] arr = initialList.toArray(new String[] {});

        for(int i = 0; i <= arr.length; i++) {
            comboList.addAll(combinations(arr, i, 0, new String[i], comboList));
        }

        comboList = sanitizeComboList(comboList);

        return comboList;
    }

    private List<String> combinations(String[] arr, int len, int startPosition, String[] result, List<String> combiList) {
        if (len == 0){
            combiList.add(Arrays.toString(result));
            return combiList;
        }
        for (int i = startPosition; i <= arr.length-len; i++){
            result[result.length - len] = arr[i];
            combinations(arr, len-1, i+1, result, combiList);
        }
        return new ArrayList<>();
    }

    private List<String> sanitizeComboList(List<String> rawComboList) {
        List<String> cleanComboList = new ArrayList<>();

        rawComboList.removeAll(Collections.singleton("[]"));

        for (String s : rawComboList) {
            String toUse = s.replace("[", "");
            toUse = toUse.replace("]", "");
            toUse = toUse.replace(",", "");
            //toUse = toUse.replace(" ", "");

            cleanComboList.add(toUse);
        }

        return cleanComboList;
    }
}
