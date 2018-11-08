package ru.glavkniga.gklients.fake;

import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.*;

import java.io.File;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Created by Vladimir on 26.05.2017.
 */
public class FakeFtpServer {

    private final org.mockftpserver.fake.FakeFtpServer ftpServer;
    private String rootPath;

    public FakeFtpServer(final int port) {
        this.ftpServer = new org.mockftpserver.fake.FakeFtpServer();
        ftpServer.setServerControlPort(port);
    }

    public FakeFtpServer withFileSystem(final String rootPath, final List<String> serverFolders) {

        this.rootPath = rootPath;

        final FileSystem fileSystem = getFileSystem();
        ftpServer.setFileSystem(fileSystem);

        fileSystem.add(new DirectoryEntry(rootPath));
        for (String folder : serverFolders) {
            folder = fileSystem instanceof WindowsFakeFileSystem ? folder.replace("/", "\\") : folder;
            final String folderPath = new File(new File(rootPath), folder).getAbsolutePath();
            final DirectoryEntry entry = new DirectoryEntry(folderPath);
            entry.setPermissions(Permissions.ALL);
            fileSystem.add(new DirectoryEntry(folderPath));
        }

        return this;
    }

    public FakeFtpServer withUser(final String username, final String password) {
        ftpServer.addUserAccount(new UserAccount(username, password, rootPath));
        return this;
    }

    public FakeFtpServer start() {
        ftpServer.start();
        return this;
    }

    public void stop() {
        ftpServer.stop();
    }

    private static FileSystem getFileSystem() {
        String os = System.getProperty("os.name");
        if (isNullOrEmpty(os)) {
            throw new IllegalStateException("Can't get OS name");
        }
        os = os.toLowerCase();
        if (os.contains("windows")) {
            return new WindowsFakeFileSystem();
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return new UnixFakeFileSystem();
        }
        throw new RuntimeException("OS is not supported");
    }
}
