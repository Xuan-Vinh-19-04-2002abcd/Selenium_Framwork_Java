package GoogleApi.googleapi;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static GoogleApi.googleapi.GoogleDriveServiceOAuth2.uploadFile;

public class GoogleSheetServiceOAuth2 {
    public static Spreadsheet createSpreadsheet(Sheets sheetsService, Drive driveService, String title) throws IOException {
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties().setTitle(title));

        try {
            spreadsheet = sheetsService.spreadsheets().create(spreadsheet)
                    .setFields("spreadsheetId,properties.title")
                    .execute();
            System.out.printf("Created spreadsheet with ID: %s and title: %s%n", spreadsheet.getSpreadsheetId(), spreadsheet.getProperties().getTitle());

            Permission publicPermission = new Permission()
                    .setType("anyone")
                    .setRole("writer");

            driveService.permissions().create(spreadsheet.getSpreadsheetId(), publicPermission).execute();
            System.out.println("Set full edit permissions for anyone with the link.");

        } catch (GoogleJsonResponseException e) {
            System.err.println("Failed to create spreadsheet: " + e.getDetails().getMessage());
            throw e;
        }
        return spreadsheet;
    }

    public static Spreadsheet createSpreadsheetInFolder(Sheets sheetsService, Drive driveService, String title, String folderId) throws IOException {
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties().setTitle(title));

        try {
            spreadsheet = sheetsService.spreadsheets().create(spreadsheet)
                    .setFields("spreadsheetId,properties.title")
                    .execute();

            String spreadsheetId = spreadsheet.getSpreadsheetId();
            System.out.printf("Created spreadsheet with ID: %s and title: %s%n", spreadsheetId, spreadsheet.getProperties().getTitle());

            if (folderId != null && !folderId.isEmpty()) {
                driveService.files().update(spreadsheetId, null)
                        .setAddParents(folderId)
                        .setRemoveParents("root")
                        .setFields("id, parents")
                        .execute();
                System.out.printf("Moved spreadsheet with ID: %s into folder ID: %s%n", spreadsheetId, folderId);
            }

            Permission publicPermission = new Permission()
                    .setType("anyone")
                    .setRole("writer");

            driveService.permissions().create(spreadsheetId, publicPermission).execute();
            System.out.println("Set full edit permissions for anyone with the link.");

        } catch (GoogleJsonResponseException e) {
            System.err.println("Failed to create spreadsheet: " + e.getDetails().getMessage());
            throw e;
        }
        return spreadsheet;
    }

    public static Spreadsheet updateSpreadsheetTitle(Sheets sheetsService, Drive driveService, String spreadsheetId, String newTitle) throws IOException {
        Request updateRequest = new Request().setUpdateSpreadsheetProperties(
                new UpdateSpreadsheetPropertiesRequest()
                        .setProperties(new SpreadsheetProperties().setTitle(newTitle))
                        .setFields("title")
        );

        sheetsService.spreadsheets().batchUpdate(spreadsheetId, new BatchUpdateSpreadsheetRequest().setRequests(Collections.singletonList(updateRequest))).execute();
        System.out.printf("Updated spreadsheet ID: %s to new title: %s%n", spreadsheetId, newTitle);

        Permission publicPermission = new Permission()
                .setType("anyone")
                .setRole("writer");

        driveService.permissions().create(spreadsheetId, publicPermission).execute();
        System.out.println("Set full edit permissions for anyone with the link.");

        return sheetsService.spreadsheets().get(spreadsheetId).execute();
    }

    public static String getSpreadsheetTitleById(Sheets sheetsService, String spreadsheetId) throws IOException {
        try {
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId)
                    .setFields("properties.title")
                    .execute();
            return spreadsheet.getProperties().getTitle();
        } catch (GoogleJsonResponseException e) {
            System.err.println("Failed to retrieve spreadsheet title: " + e.getDetails().getMessage());
            throw e;
        }
    }

    public static void deleteSpreadsheet(Drive driveService, String spreadsheetId) throws IOException {
        try {
            driveService.files().delete(spreadsheetId).execute();
            System.out.printf("Deleted spreadsheet with ID: %s%n", spreadsheetId);
        } catch (GoogleJsonResponseException e) {
            System.err.println("Failed to delete spreadsheet: " + e.getDetails().getMessage());
            throw e;
        }
    }

    public static void addSheetToSpreadsheet(Sheets sheetsService, String spreadsheetId, String sheetTitle) throws IOException {
        AddSheetRequest addSheetRequest = new AddSheetRequest()
                .setProperties(new SheetProperties().setTitle(sheetTitle));

        sheetsService.spreadsheets().batchUpdate(spreadsheetId, new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(new Request().setAddSheet(addSheetRequest)))).execute();

        System.out.printf("Added new sheet with title '%s' to spreadsheet ID: %s%n", sheetTitle, spreadsheetId);
    }

    public static void writeDataToSheet(Sheets sheetsService, String spreadsheetId, String sheetName,
                                        List<String> title, List<List<Object>> data) throws IOException {
        // Chèn title vào đầu data
        data.add(0, new ArrayList<>(title));  // Thêm title làm dòng đầu tiên

        // Tạo giá trị cho bảng tính (body chứa cả title và data)
        String range = sheetName + "!A1";
        ValueRange body = new ValueRange().setValues(data);

        // Gửi yêu cầu cập nhật dữ liệu lên Google Sheets
        sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();

        System.out.printf("Data with title written to sheet '%s' in spreadsheet ID: %s%n", sheetName, spreadsheetId);
    }
    public static List<List<Object>> generateData(int numRecords,String linkimg) {
        List<List<Object>> data = new ArrayList<>();

        // Tạo 1000 dòng dữ liệu
        for (int i = 1; i <= numRecords; i++) {
            List<Object> row = new ArrayList<>();
            // Thêm các cột dữ liệu giả vào mỗi dòng (row)
            row.add("ID-" + i); // Cột 1: ID bản ghi
            row.add(linkimg); // Cột 2: Tên giả

            data.add(row);
        }

        return data;
    }

    public static void main(String[] args) {
        try {
            Sheets sheetsService = GoogleApiOAuthServices.SheetsService();
            Drive driveService = GoogleApiOAuthServices.DriveService();
            String folderId = "1MfqscN2JnO8fCygrhRs3G2T_TIjnTG7I";
//
//            Spreadsheet sheet = createSpreadsheetInFolder(sheetsService, driveService, "stylesheetOauth4",folderId);
            String localFilePath = System.getProperty("user.dir")+"/datatest/image.jpg";
            String localFilePath1 = System.getProperty("user.dir")+"/datatest/image1.png";
            String linkimage = uploadFile(driveService,localFilePath,folderId);
            String linkimage1 = uploadFile(driveService,localFilePath1,folderId);
            ExecutorService executor = Executors.newFixedThreadPool(2);

            Callable<String> uploadTask1 = () -> uploadFile(driveService, localFilePath, folderId);
            Callable<String> uploadTask2 = () -> uploadFile(driveService, localFilePath1, folderId);

//            int numRecords = 1; // Số lượng dòng cần tạo
//            String linkimage = uploadImage(driveService,localFilePath,folderId);
//            List<List<Object>> data = generateData(numRecords,linkimage);
//            writeDataToSheet(sheetsService,sheet.getSpreadsheetId(),"Trang tính1",data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
