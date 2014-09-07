package com.deange.wkrpt300;

import com.deange.wkrpt300.model.ResponseStats;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Exporter {

    public static void orderByStartTime(final String type, final List<ResponseStats> stats) {

        Collections.sort(stats, new Comparator<ResponseStats>() {
            @Override
            public int compare(final ResponseStats lhs, final ResponseStats rhs) {
                return Double.compare(lhs.getStartMillis(), rhs.getStartMillis());
            }
        });

        output(type, stats);
    }

    public static void orderByResponseTime(final String type, final List<ResponseStats> stats) {

        Collections.sort(stats, new Comparator<ResponseStats>() {
            @Override
            public int compare(final ResponseStats lhs, final ResponseStats rhs) {
                return Double.compare(lhs.getNanoDuration(), rhs.getNanoDuration());
            }
        });

        output(type, stats);
    }


    private static void output(final String type, final List<ResponseStats> stats) {
        System.out.println("-------");
        System.out.println("Request," + type);
        for (int i = 0; i < stats.size(); i++) {
            final ResponseStats stat = stats.get(i);
            System.out.println(i + "," +
                    String.format("%.2f", (stat.getNanoDuration() / (float) 1000000)));
        }
    }

}
