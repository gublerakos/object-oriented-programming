/**
 * HW2
 */
public class HW2 {

    public static void main(String[] args) {
        
        RGBPixel pixel = new RGBPixel((short)7, (short)8, (short)1);
        
        System.out.println(pixel);
        
        pixel.setRGB((short)9, (short)7, (short)5);
        System.out.println(pixel);

    }
}