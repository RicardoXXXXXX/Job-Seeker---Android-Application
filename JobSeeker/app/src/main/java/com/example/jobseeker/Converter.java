package com.example.jobseeker;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
/**
 * This class provides methods for reading and converting CSV data to JSON format,
 * as well as merging JSON data and writing to a new JSON file.
 * @author Yunkai Xu
 */
public class Converter {
    /**
     * Reads a CSV file and returns the data as a list of maps, where each map represents a row of the CSV.
     *
     * @param csvInputStream An input stream for the CSV file.
     * @return A list of maps containing the CSV data.
     */
    public static List<Map<String, String>> readCSV(InputStream csvInputStream) {
        // Implementation reads and parses the CSV data and returns it as a list of maps.
        List<Map<String, String>> csvDataList = new ArrayList<>();
        try (InputStreamReader reader = new InputStreamReader(csvInputStream);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                Map<String, String> csvData = new HashMap<>();
                for (Map.Entry<String, Integer> header : csvParser.getHeaderMap().entrySet()) {
                    String columnName = header.getKey();
                    int columnIndex = header.getValue();
                    String cellValue = csvRecord.get(columnIndex);
                    csvData.put(columnName, cellValue);
                }
                csvDataList.add(csvData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvDataList;
    }


    /**
     * Converts a list of maps containing CSV data into a JSON string.
     *
     * @param data The list of maps to be converted to JSON.
     * @return A JSON string representing the converted data.
     */
    public static String convertToJSON(List<Map<String, String>> data) {
        // Implementation converts the CSV data to a JSON string and returns it.
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Create a new JSON object to match the structure of the existing JSON file
            ObjectNode root = objectMapper.createObjectNode();
            ArrayNode jobListings = objectMapper.createArrayNode();

            for (Map<String, String> entry : data) {
                ObjectNode jobListing = objectMapper.createObjectNode();
                jobListing.put("JobTitle", entry.get("JobTitle"));
                jobListing.put("Category", entry.get("Category"));
                jobListing.put("CompanyName", entry.get("CompanyName"));
                jobListing.put("Description", entry.get("Description"));

                // Process the Requirements field and convert it to a JSON array
                String requirements = entry.get("Requirements");
                String[] requirementsArray = requirements.split(",");
                ArrayNode requirementsNode = objectMapper.createArrayNode();
                for (String req : requirementsArray) {
                    requirementsNode.add(req.trim());
                }
                jobListing.set("Requirements", requirementsNode);

                jobListing.put("Responsibility", entry.get("Responsibility"));
                jobListing.put("SalaryRange", entry.get("SalaryRange"));

                // Process the Benefits field and convert it to a JSON array
                String benefits = entry.get("Benefits");
                String[] benefitsArray = benefits.split(",");
                ArrayNode benefitsNode = objectMapper.createArrayNode();
                for (String benefit : benefitsArray) {
                    benefitsNode.add(benefit.trim());
                }
                jobListing.set("Benefits", benefitsNode);

                jobListing.put("PostingDate", entry.get("PostingDate"));
                jobListing.put("WorkType", entry.get("WorkType"));

                // Process the Location field and convert it to a JSON array
                String location = entry.get("Location");
                String[] locationArray = location.split(",");
                ArrayNode locationNode = objectMapper.createArrayNode();
                for (String loc : locationArray) {
                    locationNode.add(loc.trim());
                }
                jobListing.set("Location", locationNode);

                jobListing.put("id", entry.get("id"));

                jobListings.add(jobListing);
            }

            root.set("jobListings", jobListings);
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Reads an existing JSON file as a string.
     *
     * @param context The Android application context.
     * @return The content of the existing JSON file as a string.
     */
    public static String loadExistingJSON(Context context) {
        // Implementation reads the content of an existing JSON file and returns it as a string.
        try {
            //Open the JSON file stream
            InputStream is = context.getResources().openRawResource(R.raw.job_seek_data);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
            reader.close();
            return jsonData.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Merges the existing JSON data with new JSON data and writes the result to a new JSON file.
     *
     * @param existingJSON The existing JSON data as a string.
     * @param newJSON The new JSON data as a string to be merged.
     * @param context The Android application context.
     */
    public static void mergeJSON(String existingJSON, String newJSON, Context context) {
        // Implementation merges the existing JSON data with new JSON data and writes it to a new JSON file.
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Parse the existing JSON
            ObjectNode existingJsonNode = objectMapper.readValue(existingJSON, ObjectNode.class);

            // Get the "jobListings" array from existing JSON
            ArrayNode existingJobListings = (ArrayNode) existingJsonNode.get("jobListings");

            // Convert the new data to ArrayNode
            ObjectNode newJsonNode = objectMapper.readValue(newJSON, ObjectNode.class);

            ArrayNode newJobListings = (ArrayNode) newJsonNode.get("jobListings");

            // Add the new data to the existing "jobListings" array
            newJobListings.addAll(existingJobListings);

            // Set the updated "jobListings" array back to the existingJsonNode
            ((ObjectNode) existingJsonNode).set("jobListings", newJobListings);

            // Specify the file path within the app's internal storage
            File file = new File(context.getFilesDir(), "newJson.json");

            // Write the updated JSON back to the file
            objectMapper.writeValue(file, existingJsonNode);

//            //file location check
//            System.out.println("Data merged and written to the file: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error merging JSON data");
        }
    }
}
