package com.korealm.reddit.imagescrapper;

// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.StringReader;
import java.io.IOException;
import java.util.HashMap;
import javax.json.*;

// TODO: Things to build in the future
// ? The download images method (maybe using ImageIO paired with BufferedImage ?)
// ? Check if would be possible to download images when they are posted in album instead of individual .extension files
// ? Create an API version of this, so it can be interacted from terminal but also from other programs
// ? Use args to trigger API version or cli version.

public class App {
    public static void main(String[] args) throws InterruptedException {

        // ! Testings
        // // The following code is for testing purposes, just to make sure to don't get banned by Reddit by too many requests in such a small portion of time.
        // String jsonString = "";
        // // Loading the json for a local file.
        // try (BufferedReader br = new BufferedReader(new FileReader("test.json"))) {
        //     while (br.ready()) {
        //         jsonString += br.readLine();
        //     }
        // }

        // JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        // JsonArray jsonChildrenArray = jsonReader.readObject().getJsonObject("data").getJsonArray("children");
        // jsonReader.close(); // Close the JsonReader. Don't forget to, pls.
        // ! Testings

        // Custom class Browser handles the HTTP connection to Reddit, and leaves everything set to run the fetchImages method, which is the one that actually fetches the images list.
        Browser browser = new Browser("Cats");
        

        try {
            browser.fetchImages(); // Inside a try catch because it throws IOException, StatusCodeException and InterruptedException (We are not using multithreading by the moment)

        } catch (IOException e) {
            System.err.println("Error: An IO Exception has occurred and this program is unable to establish a connection to the server.\nCheck that you have a working internet connection and try again.");
            return; // No point in continuing of we failed fetching the images list.
        } catch (StatusCodeException e) {
            System.err.println("Error: Failed to pull information from the server. Check if you typed a subreddit's valid name.\n");
            e.getMessage();
            return; // Same as above. No point in continuing is the list is empty....
        }

        // Create a JsonArray to read the JSON data parsing it until reaching children's Json object, which contains an array with every post information including the link to the image posted, which is what we are looking for.
        JsonArray jsonChildrenArray;

        if (browser.getJson().isPresent()) {
            jsonChildrenArray = browser.getJson().get().getJsonObject("data").getJsonArray("children");
        } else {
            System.err.println("The list of images is empty! We cannot proceed since an error has occurred somewhere close to this point, and we were unable to pull the images list from the server. Please, check the output and retry.");
            return; // If by any means the user reached here with an empty list of images, this is the last shield to protect from catastrophy.
        }
        
        HashMap<Integer, String> imageLinks = new HashMap<>();

        int index = 0;
        for (int i=0; i<jsonChildrenArray.size(); i++) {
            String temp = jsonChildrenArray.getJsonObject(i).getJsonObject("data").getJsonString("url").getString();

            if (! temp.endsWith(".jpg") && ! temp.endsWith(".jpeg") && ! temp.endsWith(".png") && ! temp.endsWith(".webp") && ! temp.endsWith(".gif")) {
                continue;
            }

            imageLinks.put(index, temp);
            index++;
        }
        
        // Print link list to ensure everything worked until this point. Congrats!
        // Next step is to build the Downloader.java module using ImageIO
        for (int key : imageLinks.keySet()) {
            System.out.println(imageLinks.get(key));
        }
    }
}
