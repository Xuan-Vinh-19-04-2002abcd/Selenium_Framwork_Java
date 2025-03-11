package GoogleApi.googleapi;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleSheetServiceByServiceAccount {
    private static final String APPLICATION_NAME = "testapp";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    public static GoogleCredential getServiceAccountCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Path to the service account key file
        FileInputStream in = new FileInputStream(System.getProperty("user.dir") + "/src/test/java/GoogleApi/credential/credential12.json");

        // Build the GoogleCredential object from the service account key
        return GoogleCredential.fromStream(in, HTTP_TRANSPORT, JSON_FACTORY)
                .createScoped(SCOPES);
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = getServiceAccountCredentials(HTTP_TRANSPORT);

        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static Spreadsheet createSpreadsheet(Sheets sheetsService, Drive driveService, String title) throws IOException {
        // Initialize the spreadsheet metadata with only the required properties
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties().setTitle(title));

        try {
            // Create the spreadsheet
            spreadsheet = sheetsService.spreadsheets().create(spreadsheet)
                    .setFields("spreadsheetId,properties.title") // Ensure there are no spaces here
                    .execute();

            System.out.printf("Created spreadsheet with ID: %s and title: %s\n", spreadsheet.getSpreadsheetId(), spreadsheet.getProperties().getTitle());

            // Set permissions to allow full access to anyone with the link
            Permission publicPermission = new Permission()
                    .setType("anyone")  // Allows anyone to access
                    .setRole("writer");  // Grants editor access

            // Apply the permission to the spreadsheet file
            driveService.permissions().create(spreadsheet.getSpreadsheetId(), publicPermission)
                    .execute();

            System.out.println("Set full edit permissions for anyone with the link.");

        } catch (GoogleJsonResponseException e) {
            System.err.println("Failed to create spreadsheet: " + e.getDetails().getMessage());
            throw e; // Re-throw after logging for clarity
        }
        return spreadsheet;
    }
    public static Spreadsheet createSpreadsheetIntoFolder(Sheets sheetsService, Drive driveService, String title, String folderId) throws IOException {
        // Initialize the spreadsheet metadata with only the required properties
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties().setTitle(title));

        try {
            // Step 1: Create the spreadsheet
            spreadsheet = sheetsService.spreadsheets().create(spreadsheet)
                    .setFields("spreadsheetId,properties.title")
                    .execute();

            String spreadsheetId = spreadsheet.getSpreadsheetId();
            System.out.printf("Created spreadsheet with ID: %s and title: %s\n", spreadsheetId, spreadsheet.getProperties().getTitle());

            // Step 2: Move the spreadsheet into the specified folder
            if (folderId != null && !folderId.isEmpty()) {
                // Use Drive API to update the parents of the file to move it into the folder
                driveService.files().update(spreadsheetId, null)
                        .setAddParents(folderId)
                        .setRemoveParents("root") // Removes from root to avoid duplication
                        .setFields("id, parents")
                        .execute();
                System.out.printf("Moved spreadsheet with ID: %s into folder ID: %s\n", spreadsheetId, folderId);
            }

            // Step 3: Set permissions to allow full access to anyone with the link
            Permission publicPermission = new Permission()
                    .setType("anyone")
                    .setRole("writer");

            driveService.permissions().create(spreadsheetId, publicPermission)
                    .execute();

            System.out.println("Set full edit permissions for anyone with the link.");

        } catch (GoogleJsonResponseException e) {
            System.err.println("Failed to create spreadsheet: " + e.getDetails().getMessage());
            throw e;
        }
        return spreadsheet;
    }

    public static Spreadsheet updateSpreadsheetTitle(Sheets sheetsService, Drive driveService, String spreadsheetId, String newTitle) throws IOException {
        // Step 1: Update the title of the spreadsheet
        Request updateRequest = new Request().setUpdateSpreadsheetProperties(
                new UpdateSpreadsheetPropertiesRequest()
                        .setProperties(new SpreadsheetProperties().setTitle(newTitle))
                        .setFields("title")
        );

        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(Collections.singletonList(updateRequest));

        sheetsService.spreadsheets().batchUpdate(spreadsheetId, body).execute();
        System.out.printf("Updated spreadsheet ID: %s to new title: %s\n", spreadsheetId, newTitle);

        // Step 2: Set full permissions for anyone with the link
        Permission publicPermission = new Permission()
                .setType("anyone")
                .setRole("writer");

        driveService.permissions().create(spreadsheetId, publicPermission)
                .execute();

        System.out.println("Set full edit permissions for anyone with the link.");

        // Step 3: Retrieve the updated spreadsheet details without setFields for testing
        Spreadsheet updatedSpreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        System.out.printf("Retrieved spreadsheet with ID: %s and title: %s\n", updatedSpreadsheet.getSpreadsheetId(), updatedSpreadsheet.getProperties().getTitle());

        return updatedSpreadsheet;
    }

    public static String getSpreadsheetTitleById(Sheets service, String spreadsheetId) throws IOException {
        try {
            // Retrieve the spreadsheet title
            Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId)
                    .setFields("properties.title") // Only retrieve the 'title' field
                    .execute();

            String title = spreadsheet.getProperties().getTitle();
            System.out.printf("Spreadsheet ID: %s has title: %s\n", spreadsheetId, title);
            return title;

        } catch (GoogleJsonResponseException e) {
            System.err.println("Failed to retrieve spreadsheet title: " + e.getDetails().getMessage());
            throw e;
        }
    }
    public static void deleteSpreadsheet(Drive driveService, String spreadsheetId) throws IOException {
        try {
            // Use the Drive API to delete the spreadsheet by its ID
            driveService.files().delete(spreadsheetId).execute();
            System.out.printf("Deleted spreadsheet with ID: %s\n", spreadsheetId);
        } catch (GoogleJsonResponseException e) {
            System.err.println("Failed to delete spreadsheet: " + e.getDetails().getMessage());
            throw e;
        }
    }
    public static void addSheetToSpreadsheet(Sheets sheetsService, String spreadsheetId, String sheetTitle) throws IOException {
        // Create a new AddSheetRequest with the desired sheet title
        AddSheetRequest addSheetRequest = new AddSheetRequest()
                .setProperties(new SheetProperties().setTitle(sheetTitle));

        // Wrap the request in a BatchUpdateSpreadsheetRequest
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(new Request().setAddSheet(addSheetRequest)));

        // Execute the request
        sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();

        System.out.printf("Added new sheet with title '%s' to spreadsheet ID: %s\n", sheetTitle, spreadsheetId);
    }
    public static void writeDataToSheet(Sheets sheetsService, String spreadsheetId, String sheetName, List<List<Object>> data) throws IOException {
        // Specify the range in A1 notation (e.g., "Sheet1!A1" to start at cell A1 of "Sheet1")
        String range = sheetName + "!A1"; // Adjust "A1" as needed for specific starting cells

        // Create the data to write in a ValueRange object
        ValueRange body = new ValueRange().setValues(data);

        // Write data to the specified range
        sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW") // Use "RAW" to input data as is, or "USER_ENTERED" for evaluated input
                .execute();

        System.out.printf("Data written to sheet '%s' in spreadsheet ID: %s\n", sheetName, spreadsheetId);
    }
    public static void main(String[] args) {
        try {
            Sheets sheetsService = GoogleSheetServiceByServiceAccount.getSheetsService();

            String spreadsheetId = "1JLp4u_sL2Dpf0lDR7uEkefJJ4Wi1203kZtLJtMnLQQ4"; // Replace with your actual spreadsheet ID
            String sheetName = "Trang-tính2"; // The name of the sheet (tab) you want to write data to

            // Sample data to write
            List<List<Object>> data = List.of(
                    List.of("Name", "Age", "City"),
                    List.of("Alice", 30, "New York"),
                    List.of("Bob", 25, "Los Angeles"),
                    List.of("Charlie", 35, "Chicago")
            );

            writeDataToSheet(sheetsService, spreadsheetId, sheetName, data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
