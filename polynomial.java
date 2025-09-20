import java.util.*;
import java.math.BigInteger;

class Main {
    public static void main(String[] args) {

        List<List<String>> points = new ArrayList<>();  
        String jsonString = """
{
"keys": {
    "n": 10,
    "k": 7
  },
  "1": {
    "base": "6",
    "value": "13444211440455345511"
  },
  "2": {
    "base": "15",
    "value": "aed7015a346d635"
  },
  "3": {
    "base": "15",
    "value": "6aeeb69631c227c"
  },
  "4": {
    "base": "16",
    "value": "e1b5e05623d881f"
  },
  "5": {
    "base": "8",
    "value": "316034514573652620673"
  },
  "6": {
    "base": "3",
    "value": "2122212201122002221120200210011020220200"
  },
  "7": {
    "base": "3",
    "value": "20120221122211000100210021102001201112121"
  },
  "8": {
    "base": "6",
    "value": "20220554335330240002224253"
  },
  "9": {
    "base": "12",
    "value": "45153788322a1255483"
  },
  "10": {
    "base": "7",
    "value": "1101613130313526312514143"
  }
}
    """;

        jsonString = jsonString.replace("{", "")
                               .replace("}", "")
                               .replace("\"", "")
                               .trim();

        String[] lines = jsonString.split("\n");

        int k = 0;
        List<String> keys = new ArrayList<>();
        List<String> bases = new ArrayList<>();
        List<String> values = new ArrayList<>();

        String currentKey = null;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (!line.contains(":")) continue;

            String[] parts = line.split(":");
            String keyPart = parts[0].trim();
            String valuePart = parts.length > 1 ? parts[1].trim().replace(",", "") : "";

            if (keyPart.equals("k")) {
                k = Integer.parseInt(valuePart);
            } else if (keyPart.matches("\\d+")) {
                currentKey = keyPart;
                keys.add(currentKey);
            } else if (keyPart.equals("base")) {
                bases.add(valuePart);
            } else if (keyPart.equals("value")) {
                values.add(valuePart);
            }
        }

        // Collect raw points
        for (int i = 0; i < keys.size(); i++) {
            List<String> in = new ArrayList<>();
            in.add(keys.get(i));    // x
            in.add(bases.get(i));   // base
            in.add(values.get(i));  // value
            points.add(in);
        }

        // Decode to BigInteger points
        List<List<BigInteger>> x_y_points = decode(points);
     

        BigInteger ans = lagrange(x_y_points, k);
        System.out.println("Secret constant term: " + ans);
    }

    // Decode values to base-10 BigInteger
    public static List<List<BigInteger>> decode(List<List<String>> p) {
        List<List<BigInteger>> points = new ArrayList<>();
        List<BigInteger> x_values = new ArrayList<>();
        List<BigInteger> y_values = new ArrayList<>();

        for (List<String> lst : p) {
            BigInteger x = new BigInteger(lst.get(0));   // x = key
            int base = Integer.parseInt(lst.get(1));     // base
            String valueStr = lst.get(2);                // value in given base

            // Convert value from its base to BigInteger
            BigInteger y = new BigInteger(valueStr, base);

            x_values.add(x);
            y_values.add(y);

        }

        points.add(x_values);
        points.add(y_values);
        return points;
    }

    // Lagrange interpolation using BigInteger
    public static BigInteger lagrange(List<List<BigInteger>> points, int k) {
        List<BigInteger> x_values = points.get(0);
        List<BigInteger> y_values = points.get(1);

        BigInteger constant = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    num = num.multiply(BigInteger.ZERO.subtract(x_values.get(j)));
                    den = den.multiply(x_values.get(i).subtract(x_values.get(j)));
                }
            }

            BigInteger term = y_values.get(i).multiply(num).divide(den);
            constant = constant.add(term);

          
        }

        return constant;
    }
}

