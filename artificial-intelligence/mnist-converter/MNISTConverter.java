
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class MNISTConverter {

    public static void convert(String imgFileName, String labelFileName, String outFileName, int n) throws IOException {
        try (FileInputStream f = new FileInputStream(imgFileName); 
             FileWriter o = new FileWriter(outFileName); 
             FileInputStream l = new FileInputStream(labelFileName)) {

            final StringBuilder firstLine = new StringBuilder("label,");
            for (int i = 1; i < 29; i++) {
                for (int j = 1; j < 29; j++) {
                    firstLine.append(i).append("x").append(j);
                    if (i != 28 || j != 28) {
                        firstLine.append(",");
                    }
                }
            }
            o.write(firstLine.toString() + "\n");

            byte[] bytes = new byte[16];
            f.read(bytes, 0, 16); // skip the first 16 bytes
            l.read(bytes, 0, 8);  // skip the first 8 bytes

            for (int i = 0; i < n; i++) {
                StringBuilder image = new StringBuilder();
                image.append(l.read() & 0xFF); // read label

                for (int j = 0; j < 28 * 28; j++) {
                    image.append(",").append(f.read() & 0xFF); // read image bytes
                }

                o.write(image.toString() + "\n");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        convert("train-images-idx3-ubyte", "train-labels-idx1-ubyte", "mnist_fashion_train.csv", 60000);
        convert("t10k-images-idx3-ubyte", "t10k-labels-idx1-ubyte", "mnist_fashion_test.csv", 10000);
    }
}
