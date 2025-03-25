import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

class TaxiTrip {
    String pickupDatetime;
    String dropoffDatetime;
    int vendorID;
    int passengerCount;
    double tripDistance;
    int paymentType;
    double fareAmount;
    double tipAmount;
    String storeAndFwdFlag;

    public TaxiTrip(String pickupDatetime, String dropoffDatetime, int vendorID, int passengerCount,
                    double tripDistance, int paymentType, double fareAmount, double tipAmount, String storeAndFwdFlag) {
        this.pickupDatetime = pickupDatetime;
        this.dropoffDatetime = dropoffDatetime;
        this.vendorID = vendorID;
        this.passengerCount = passengerCount;
        this.tripDistance = tripDistance;
        this.paymentType = paymentType;
        this.fareAmount = fareAmount;
        this.tipAmount = tipAmount;
        this.storeAndFwdFlag = storeAndFwdFlag;
    }
}

public class JsonFileReader {
    public static void main(String[] args) {
        
        if (args.length < 2) {
            System.out.println("Usage: java JsonFileReader <query_name> <filepath>");
            return;
        }

        String filePath = args[0];
        String queryName = args[1];

        File file = new File(filePath);
        if (file.length() == 0) {
            System.out.println("Error: File is empty!");
            return;
        }


        switch (queryName) {
            case "query1":
                runQuery1(filePath);
                break;
            case "query2":
                runQuery2(filePath);
                break;
            case "query3":
                runQuery3(filePath);
                break;
            case "query4":
                runQuery4(filePath);
                break;
            default:
                System.out.println("Invalid query name. Use query1, query2, query3, or query4.");
        }
    }

    private static void runQuery1(String filePath) {
        int totalTrips = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath), 8192)) {
            while (br.readLine() != null) {
                totalTrips++;
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        System.out.println("\n=== Query 1: Total Trips Count ===");
        System.out.println("Total Trips: " + totalTrips);
    }

    private static void runQuery2(String filePath) {
        Map<Integer, Integer> tripCountByPayment = new HashMap<>();
        Map<Integer, Double> fareSumByPayment = new HashMap<>();
        Map<Integer, Double> tipSumByPayment = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            Stream<String> lines = br.lines();
            lines.forEach(line -> {
                TaxiTrip trip = parseJsonLine(line);
                if (trip != null && trip.tripDistance > 5) {
                    tripCountByPayment.merge(trip.paymentType, 1, Integer::sum);
                    fareSumByPayment.merge(trip.paymentType, trip.fareAmount, Double::sum);
                    tipSumByPayment.merge(trip.paymentType, trip.tipAmount, Double::sum);
                }
            });
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        System.out.println("\n=== Query 2: Trips > 5 miles Grouped by Payment Type ===");
        System.out.printf("%-12s %-12s %-12s %-12s\n", "PaymentType", "NumTrips", "AvgFare", "TotalTip");
        for (int paymentType : tripCountByPayment.keySet()) {
            double avgFare = fareSumByPayment.get(paymentType) / tripCountByPayment.get(paymentType);
            System.out.printf("%-12d %-12d $%-11.2f $%-11.2f\n",
                    paymentType,
                    tripCountByPayment.get(paymentType),
                    avgFare,
                    tipSumByPayment.get(paymentType));
        }
    }


    private static void runQuery3(String filePath) {
        Map<Integer, Integer> tripCountByVendor = new HashMap<>();
        Map<Integer, Integer> passengerSumByVendor = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                TaxiTrip trip = parseJsonLine(line);
                if (trip != null && trip.storeAndFwdFlag.equals("Y") && trip.pickupDatetime.startsWith("2024-01")) {
                    tripCountByVendor.merge(trip.vendorID, 1, Integer::sum);
                    passengerSumByVendor.merge(trip.vendorID, trip.passengerCount, Integer::sum);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        System.out.println("\n=== Query 3: Store-and-Forward Trips Grouped by Vendor ===");
        System.out.printf("%-10s %-10s %-15s\n", "VendorID", "Trips", "AvgPassengers");
        for (int vendorID : tripCountByVendor.keySet()) {
            double avgPassengers = (double) passengerSumByVendor.get(vendorID) / tripCountByVendor.get(vendorID);
            System.out.printf("%-10d %-10d %-15.2f\n", vendorID, tripCountByVendor.get(vendorID), avgPassengers);
        }
    }

    private static void runQuery4(String filePath) {
        Map<String, Integer> tripCountByDate = new HashMap<>();
        Map<String, Integer> passengerSumByDate = new HashMap<>();
        Map<String, Double> distanceSumByDate = new HashMap<>();
        Map<String, Double> fareSumByDate = new HashMap<>();
        Map<String, Double> tipSumByDate = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) 
            {
                TaxiTrip trip = parseJsonLine(line);
                if (trip == null) continue; //skip invalid lines

                String tripDate = trip.pickupDatetime.split(" ")[0];

                if (tripDate.startsWith("2024-01")) {
                    tripCountByDate.merge(tripDate, 1, Integer::sum);
                    passengerSumByDate.merge(tripDate, trip.passengerCount, Integer::sum);
                    distanceSumByDate.merge(tripDate, trip.tripDistance, Double::sum);
                    fareSumByDate.merge(tripDate, trip.fareAmount, Double::sum);
                    tipSumByDate.merge(tripDate, trip.tipAmount, Double::sum);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    
        System.out.println("\n=== Query 4: Daily Statistics for January 2024 ===");
        System.out.printf("%-12s %-12s %-15s %-15s %-15s %-15s\n", "Date", "Trips", "AvgPassengers", "AvgDistance", "AvgFare", "TotalTip");

        for (String tripDate : tripCountByDate.keySet()) {
            int count = tripCountByDate.get(tripDate);
            double avgPassengers = (double) passengerSumByDate.get(tripDate) / count;
            double avgDistance = distanceSumByDate.get(tripDate) / count;
            double avgFare = fareSumByDate.get(tripDate) / count;
            double totalTip = tipSumByDate.get(tripDate);

            System.out.printf("%-12s %-12d %-15.2f %-15.2f %-15.2f %-15.2f\n",
                tripDate, count, avgPassengers, avgDistance, avgFare, totalTip);
            }
    }


    private static TaxiTrip parseJsonLine(String json) {
        try {
            String[] fields = json.replaceAll("[{}\"]", "").split(",");

            if (fields.length < 9) return null;

            String pickupDatetime = fields[0].split(":")[1] + ":" + fields[0].split(":")[2];
            String dropoffDatetime = fields[1].split(":")[1] + ":" + fields[1].split(":")[2];
            int vendorID = parseSafeInt(fields[2].split(":")[1]);
            int passengerCount = parseSafeInt(fields[3].split(":")[1]);
            double tripDistance = parseSafeDouble(fields[4].split(":")[1]);
            int paymentType = parseSafeInt(fields[5].split(":")[1]);
            double fareAmount = parseSafeDouble(fields[6].split(":")[1]);
            double tipAmount = parseSafeDouble(fields[7].split(":")[1]);
            String storeAndFwdFlag = fields[8].split(":")[1];

            return new TaxiTrip(pickupDatetime, dropoffDatetime, vendorID, passengerCount, tripDistance, paymentType, fareAmount, tipAmount, storeAndFwdFlag);
        } catch (Exception e) {
            System.out.println("Skipping invalid JSON line: " + json);
            return null;
        }
    }

    private static int parseSafeInt(String value) {
        if (value == null || value.equals("null") || value.isEmpty()) return 0;
        return Integer.parseInt(value);
    }

    private static double parseSafeDouble(String value) {
        if (value == null || value.equals("null") || value.isEmpty()) return 0.0;
        return Double.parseDouble(value);
    }
}