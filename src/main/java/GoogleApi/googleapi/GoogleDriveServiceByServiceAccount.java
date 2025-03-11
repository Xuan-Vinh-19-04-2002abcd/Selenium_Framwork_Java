package GoogleApi.googleapi;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDriveServiceByServiceAccount {
    private static final String APPLICATION_NAME = "testapp";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    public static GoogleCredential getServiceAccountCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Path to the service account key file
        FileInputStream in = new FileInputStream(System.getProperty("user.dir") + "/src/test/java/GoogleApi/credential/credential12.json");

        // Build the GoogleCredential object from the service account key
        return GoogleCredential.fromStream(in, HTTP_TRANSPORT, JSON_FACTORY)
                .createScoped(SCOPES);
    }

    public static Drive getDriveService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = getServiceAccountCredentials(HTTP_TRANSPORT);

        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static File createFolder(Drive service, String folderName, String parentFolderId) throws IOException {
        // Metadata for the folder
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        if (parentFolderId != null && !parentFolderId.isEmpty()) {
            fileMetadata.setParents(Collections.singletonList(parentFolderId));
        }

        // Create the folder
        File folder = service.files().create(fileMetadata)
                .setFields("id, name")
                .execute();

        // Set folder permissions to public
        Permission publicPermission = new Permission()
                .setType("anyone") // Allows anyone to access
                .setRole("reader"); // Set to reader; can change to "writer" if you want edit access

        service.permissions().create(folder.getId(), publicPermission)
                .execute();

        System.out.printf("Created folder with ID: %s and set to public access\n", folder.getId());
        return folder;
    }
    public static void deleteFolder(Drive service, String folderId) throws IOException {
        service.files().delete(folderId).execute();
        System.out.printf("Deleted folder with ID: %s\n", folderId);
    }
    public static File updateFolderName(Drive service, String folderId, String newFolderName) throws IOException {
        // Create a file metadata object with the new name
        File fileMetadata = new File();
        fileMetadata.setName(newFolderName);

        // Update the folder
        File updatedFolder = service.files().update(folderId, fileMetadata)
                .setFields("id, name")
                .execute();

        System.out.printf("Updated folder ID: %s with new name: %s\n", folderId, updatedFolder.getName());
        return updatedFolder;
    }
    public static String getFolderNameById(Drive service, String folderId) throws IOException {
        File folder = service.files().get(folderId)
                .setFields("name") // Only retrieve the 'name' field
                .execute();

        System.out.printf("Folder ID: %s has name: %s\n", folderId, folder.getName());
        return folder.getName();
    }
    public static void main(String[] args) {
        try {
            Drive service = GoogleDriveServiceByServiceAccount.getDriveService();
            createFolder(service,"abcdd","1IEBLGAV77UtV7dQ2im86V26y8V1pULXq");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
