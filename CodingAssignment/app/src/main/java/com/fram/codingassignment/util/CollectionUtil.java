package com.fram.codingassignment.util;

import java.util.List;

/**
 * Created by thaile on 6/5/17.
 */

public class CollectionUtil {

    public static int partition(int A[], int si, int ei) {
        int x = A[ei];
        int i = (si - 1);
        int j;

        for (j = si; j <= ei - 1; j++) {
            if (A[j] <= x) {
                i++;
                int temp = A[i];
                A[i] = A[j];
                A[j] = temp;
            }
        }
        int temp = A[i + 1];
        A[i + 1] = A[ei];
        A[ei] = temp;
        return (i + 1);
    }

    public static void quickSort(int A[], int si, int ei) {
        int pi;

		/* Partitioning index */
        if (si < ei) {
            pi = partition(A, si, ei);
            quickSort(A, si, pi - 1);
            quickSort(A, pi + 1, ei);
        }
    }

    public static int partition(List<Integer> array, int si, int ei) {
        int x = array.get(ei);
        int i = (si - 1);
        int j;

        for (j = si; j <= ei - 1; j++) {
            if (array.get(j) <= x) {
                i++;
                int temp = array.get(i);
                array.add(i, array.get(j));
                array.add(j, temp);
            }
        }
        int temp = array.get(i + 1);
        array.add(i + 1, array.get(ei));
        array.add(ei, temp);
        return (i + 1);
    }

    public static void quickSort(List<Integer> array, int si, int ei) {
        int pi;

		/* Partitioning index */
        if (si < ei) {
            pi = partition(array, si, ei);
            quickSort(array, si, pi - 1);
            quickSort(array, pi + 1, ei);
        }
    }

    public static int interpolationSearch(List<Integer> array, int x) {
        int lo = 0;
        int mid = -1;
        int hi = array.size() - 1;
        int rs = -1;
        while (rs == -1) {
            if (lo == hi || array.get(lo) == array.get(hi)) {
                rs = -1;
                break;
            }
            mid = lo + ((hi - lo) / (array.get(hi) - array.get(lo)) * (x - array.get(lo)));
            if (array.get(mid) == x) {
                rs = mid;
            } else {
                if (array.get(mid) < x) {
                    lo = mid + 1;
                } else if (array.get(mid) > x) {
                    hi = mid - 1;
                }
            }
        }
        return rs;
    }
}
