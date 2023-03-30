/**
 * @author trevor hartman
 * @author michael thoreson
 * @since 1.0
 */

import org.apache.commons.codec.digest.Crypt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.stream.Stream;

public class Crack {
    private final User[] users;
    private final String dictionary;

    public Crack(String shadowFile, String dictionary) throws FileNotFoundException {
        this.dictionary = dictionary;
        this.users = Crack.parseShadow(shadowFile);
    }

    public void crack() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(this.dictionary);
        Scanner scanner = new Scanner(fileInputStream);
        while(scanner.hasNext()){
            String word = scanner.nextLine();
            for(int i=0; i < this.users.length; i++) {
                if(!this.users[i].getPassHash().contains("$")){continue;}
                String currentHash = Crypt.crypt(word, this.users[i].getPassHash());
                if (this.users[i].getPassHash().equals(currentHash)){
                    System.out.printf("Found password %s for user %s%n", word ,this.users[i].getUsername());
                }
            }
        }
    }

    public static int getLineCount(String path) {
        int lineCount = 0;
        try (Stream<String> stream = Files.lines(Path.of(path), StandardCharsets.UTF_8)) {
            lineCount = (int)stream.count();
        } catch(IOException ignored) {}
        return lineCount;
    }

    public static User[] parseShadow(String shadowFile) throws FileNotFoundException {
        User[] users = new User[getLineCount(shadowFile)];
        FileInputStream fileInputStream = new FileInputStream(shadowFile);
        Scanner scanner = new Scanner(fileInputStream);
        int i = 0;

        while(scanner.hasNextLine()) {
            String index = scanner.nextLine();
            String split[] = index.split(":");
//            User currentUser = new User(split[0],split[1]);
            users[i] = new User(split[0], split[1]);
            i +=1;
        }
        return users;


    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Type the path to your shadow file: ");
        String shadowPath = sc.nextLine();
        System.out.print("Type the path to your dictionary file: ");
        String dictPath = sc.nextLine();

        Crack c = new Crack(shadowPath, dictPath);
        c.crack();
    }
}
