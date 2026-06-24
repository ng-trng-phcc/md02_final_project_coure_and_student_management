package utils;

import java.util.List;

public class ConsoleUI {
    public static final String RESET = "\u001B[0m";
    public static final String BLUE = "\u001B[38;2;97;175;239m";
    public static final String GRAY = "\u001B[38;2;92;99;112m";
    public static final String LIGHT_GRAY = "\u001B[38;2;171;178;191m";
    public static final String CYAN = "\u001B[38;2;86;182;194m";
    public static final String GREEN = "\u001B[38;2;152;195;121m";
    public static final String YELLOW = "\u001B[38;2;229;192;123m";
    public static final String RED = "\u001B[38;2;224;108;117m";
    public static final String BOLD = "\u001B[1m";

    public static final int WIDTH = 52;
    public static final String TOP_LEFT = "\u256D";
    public static final String TOP_RIGHT = "\u256E";
    public static final String BOTTOM_LEFT = "\u2570";
    public static final String BOTTOM_RIGHT = "\u256F";
    public static final String HORIZONTAL = "\u2500";
    public static final String VERTICAL = "\u2502";
    public static final String T_DOWN = "\u252C";
    public static final String T_UP = "\u2534";
    public static final String CROSS_LEFT = "\u251C";
    public static final String CROSS_RIGHT = "\u2524";

    // Table-drawing characters (straight corners)
    public static final String TBL_TL = "\u250C";
    public static final String TBL_TR = "\u2510";
    public static final String TBL_BL = "\u2514";
    public static final String TBL_BR = "\u2518";
    public static final String CROSS = "\u253C";

    public static String repeat(String s, int n) {
        return s.repeat(Math.max(0, n));
    }

    private static String centered(String text, int width) {
        int textLen = 0;
        String clean = text.replaceAll("\u001B\\[[;\\d]*m", "");
        textLen = clean.length();
        if (textLen >= width) return text;
        int left = (width - textLen) / 2;
        int right = width - textLen - left;
        return repeat(" ", left) + text + repeat(" ", right);
    }

    private static String padRight(String text, int width) {
        int textLen = 0;
        String clean = text.replaceAll("\u001B\\[[;\\d]*m", "");
        textLen = clean.length();
        if (textLen >= width) return text;
        return text + repeat(" ", width - textLen);
    }

    public static void printLine(String content) {
        System.out.println(GRAY + VERTICAL + RESET + "  " + content + repeat(" ", WIDTH - 4 - visibleLen(content)) + GRAY + VERTICAL + RESET);
    }

    private static int visibleLen(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    public static void printEmptyLine() {
        System.out.println(GRAY + VERTICAL + RESET + repeat(" ", WIDTH - 2) + GRAY + VERTICAL + RESET);
    }

    public static void printSeparator() {
        System.out.println(GRAY + CROSS_LEFT + repeat(HORIZONTAL, WIDTH - 2) + CROSS_RIGHT + RESET);
    }

    public static void printTopBar() {
        System.out.println(GRAY + TOP_LEFT + repeat(HORIZONTAL, WIDTH - 2) + TOP_RIGHT + RESET);
    }

    public static void printBottomBar() {
        System.out.println(GRAY + BOTTOM_LEFT + repeat(HORIZONTAL, WIDTH - 2) + BOTTOM_RIGHT + RESET);
    }

    public static void printCentered(String text) {
        int cleanLen = text.replaceAll("\u001B\\[[;\\d]*m", "").length();
        int left = (WIDTH - 2 - cleanLen) / 2;
        int right = WIDTH - 2 - cleanLen - left;
        System.out.println(GRAY + VERTICAL + RESET + repeat(" ", left) + text + repeat(" ", right) + GRAY + VERTICAL + RESET);
    }

    public static void printMenuTitle(String title) {
        printTopBar();
        printCentered(BLUE + BOLD + title + RESET);
        printSeparator();
        printEmptyLine();
    }

    public static void printMenuOption(int num, String label) {
        printLine(CYAN + num + RESET + "  " + LIGHT_GRAY + label + RESET);
    }

    public static void printMenuOption(String key, String label) {
        printLine(CYAN + key + RESET + "  " + LIGHT_GRAY + label + RESET);
    }

    public static void printMenuFooter() {
        printEmptyLine();
        printBottomBar();
    }

    public static void printPrompt() {
        System.out.print(CYAN + "> " + RESET);
    }

    public static void printBoxMenu(String title, String... options) {
        printMenuTitle(title);
        for (String opt : options) {
            printLine("  " + LIGHT_GRAY + opt + RESET);
        }
        printMenuFooter();
        printPrompt();
    }

    public static void info(String msg) {
        System.out.println(BLUE + BOLD + "[INFO] " + RESET + msg);
    }

    public static void success(String msg) {
        System.out.println(GREEN + BOLD + "[SUCCESS] " + RESET + msg);
    }

    public static void warning(String msg) {
        System.out.println(YELLOW + BOLD + "[WARNING] " + RESET + msg);
    }

    public static void error(String msg) {
        System.out.println(RED + BOLD + "[ERROR] " + RESET + msg);
    }

    public static void printTable(String title, String[] headers, List<String[]> data) {
        if (headers == null || headers.length == 0) return;
        int cols = headers.length;

        String coloredTitle = (title != null && !title.isEmpty()) ? BLUE + BOLD + title + RESET : null;
        String[] coloredHeaders = new String[cols];
        for (int i = 0; i < cols; i++) {
            coloredHeaders[i] = CYAN + headers[i] + RESET;
        }

        int[] widths = new int[cols];
        for (int i = 0; i < cols; i++) {
            widths[i] = visibleLen(coloredHeaders[i]) + 2;
        }
        for (String[] row : data) {
            for (int i = 0; i < Math.min(cols, row.length); i++) {
                widths[i] = Math.max(widths[i], visibleLen(row[i] != null ? row[i] : "") + 2);
            }
        }

        int total = 1;
        for (int i = 0; i < cols; i++) total += widths[i] + (i < cols - 1 ? 1 : 0);
        total += 1;

        System.out.println(GRAY + TBL_TL + repeat(HORIZONTAL, total - 2) + TBL_TR + RESET);
        if (coloredTitle != null) {
            int titleLen = visibleLen(coloredTitle);
            int inner = total - 2;
            if (titleLen <= inner) {
                int left = (inner - titleLen) / 2;
                int right = inner - titleLen - left;
                System.out.println(GRAY + VERTICAL + RESET + repeat(" ", left) + coloredTitle + repeat(" ", right) + GRAY + VERTICAL + RESET);
            } else {
                System.out.println(GRAY + VERTICAL + RESET + " " + coloredTitle + " " + GRAY + VERTICAL + RESET);
            }
            System.out.print(GRAY + CROSS_LEFT + RESET);
            for (int i = 0; i < cols; i++) {
                System.out.print(GRAY + repeat(HORIZONTAL, widths[i]) + RESET);
                if (i < cols - 1) System.out.print(GRAY + T_DOWN + RESET);
            }
            System.out.println(GRAY + CROSS_RIGHT + RESET);
        }

        System.out.print(GRAY + VERTICAL + RESET);
        for (int i = 0; i < cols; i++) {
            String h = coloredHeaders[i];
            int pad = widths[i] - visibleLen(h);
            int left = pad / 2;
            int right = pad - left;
            System.out.print(repeat(" ", left) + h + repeat(" ", right));
            System.out.print(GRAY + VERTICAL + RESET);
        }
        System.out.println();

        System.out.print(GRAY + CROSS_LEFT + RESET);
        for (int i = 0; i < cols; i++) {
            System.out.print(GRAY + repeat(HORIZONTAL, widths[i]) + RESET);
            if (i < cols - 1) System.out.print(GRAY + CROSS + RESET);
        }
        System.out.println(GRAY + CROSS_RIGHT + RESET);

        for (String[] row : data) {
            System.out.print(GRAY + VERTICAL + RESET);
            for (int i = 0; i < cols; i++) {
                String val = i < row.length ? (row[i] != null ? row[i] : "") : "";
                int len = visibleLen(val);
                System.out.print(" " + val + repeat(" ", widths[i] - len - 1));
                System.out.print(GRAY + VERTICAL + RESET);
            }
            System.out.println();
        }

        System.out.print(GRAY + TBL_BL + RESET);
        for (int i = 0; i < cols; i++) {
            System.out.print(GRAY + repeat(HORIZONTAL, widths[i]) + RESET);
            if (i < cols - 1) System.out.print(GRAY + T_UP + RESET);
        }
        System.out.println(GRAY + TBL_BR + RESET);
    }

    public static void printHeader(String title) {
        printTopBar();
        printCentered(BLUE + BOLD + title + RESET);
        printSeparator();
    }

    public static void printFooter() {
        printBottomBar();
    }
}
