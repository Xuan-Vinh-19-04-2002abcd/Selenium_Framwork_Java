package GoogleApi.googleapi;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class GoogleDriveServiceOAuth2 {
    public static File createFolder(Drive service, String folderName, String parentFolderId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        if (parentFolderId != null && !parentFolderId.isEmpty()) {
            fileMetadata.setParents(Collections.singletonList(parentFolderId));
        }

        File folder = service.files().create(fileMetadata)
                .setFields("id, name")
                .execute();

        Permission publicPermission = new Permission()
                .setType("anyone")
                .setRole("reader");

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
        File fileMetadata = new File();
        fileMetadata.setName(newFolderName);

        File updatedFolder = service.files().update(folderId, fileMetadata)
                .setFields("id, name")
                .execute();

        System.out.printf("Updated folder ID: %s with new name: %s\n", folderId, updatedFolder.getName());
        return updatedFolder;
    }

    public static String getFolderNameById(Drive service, String folderId) throws IOException {
        File folder = service.files().get(folderId)
                .setFields("name")
                .execute();

        System.out.printf("Folder ID: %s has name: %s\n", folderId, folder.getName());
        return folder.getName();
    }
    public static String getFolderIdByName(Drive service, String folderName) throws IOException {
        // Query to search for a folder with the specified name
        String query = "name = '" + folderName + "' and mimeType = 'application/vnd.google-apps.folder'";

        // Execute the search
        FileList result = service.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();

        // Check if any folders were found
        if (!result.getFiles().isEmpty()) {
            File folder = result.getFiles().get(0); // Get the first result
            System.out.printf("Folder Name: %s has ID: %s\n", folderName, folder.getId());
            return folder.getId();
        } else {
            System.out.printf("No folder found with name: %s\n", folderName);
            return null;
        }
    }

    public static String uploadFile(Drive service, String localFilePath, String folderId) throws IOException {
        java.io.File filePath = new java.io.File(localFilePath);

        // Detect MIME type dynamically based on the file extension
        String mimeType = Files.probeContentType(Paths.get(localFilePath));
        if (mimeType == null) {
            throw new IOException("Could not detect MIME type for file: " + localFilePath);
        }

        File fileMetadata = new File();
        fileMetadata.setName(filePath.getName());

        // Set parent folder if provided
        if (folderId != null && !folderId.isEmpty()) {
            fileMetadata.setParents(Collections.singletonList(folderId));
        }

        FileContent mediaContent = new FileContent(mimeType, filePath);

        // Upload the file to Google Drive
        File uploadedFile = service.files().create(fileMetadata, mediaContent)
                .setFields("id, name")
                .execute();

        // Make the file publicly accessible
        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");
        service.permissions().create(uploadedFile.getId(), permission).execute();

        // Generate and return the file's URL
        String fileUrl = "https://drive.google.com/uc?id=" + uploadedFile.getId();
        System.out.printf("Uploaded file with ID: %s and name: %s\n", uploadedFile.getId(), uploadedFile.getName());
        System.out.printf("File URL: %s\n", fileUrl);

        return fileUrl;
    }

    public static void main(String[] args) {
        try {
            Drive driveService = GoogleApiOAuthServices.DriveService();
//            createFolder(driveService,"TestFolderByOAuth1","1MfqscN2JnO8fCygrhRs3G2T_TIjnTG7I");
//            updateFolderName(driveService,"1WVCpw2lewv63s4fU_F2qBPAah0BJFkOI","TestOauthClienUpdated");
//            deleteFolder(driveService,"1WVCpw2lewv63s4fU_F2qBPAah0BJFkOI");
////            getFolderNameById(driveService,"1XXne4480S0tFVPCkvHCZHnPC33CY6B7w");
//            getFolderIdByName(driveService,"abc");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
