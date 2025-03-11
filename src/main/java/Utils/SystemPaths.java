package Utils;

public class SystemPaths {
    public static final String HOME_DIRECTORY = System.getProperty("user.home");

    public static final String DESKTOP = HOME_DIRECTORY + "/Desktop";
    public static final String DOCUMENTS = HOME_DIRECTORY + "/Documents";
    public static final String DOWNLOADS = HOME_DIRECTORY + "/Downloads";

    public static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    public static final boolean IS_WINDOWS = OS_NAME.contains("win");
    public static final String CURRENT_DIRECTORY = System.getProperty("user.dir");

    static {
        if (IS_WINDOWS) {
            // Đổi đường dẫn thành chuẩn Windows (nếu cần)
            System.setProperty("file.separator", "\\");
        }
    }

    // Ngăn không cho tạo instance của class này
    private SystemPaths() {
        throw new UnsupportedOperationException("Đây là class chứa hằng số, không thể khởi tạo!");
    }
}
