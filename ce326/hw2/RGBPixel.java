/**
 * RGBPixel
 */
public class RGBPixel {
    private int RGB;
    private int red;
    private int green;
    private int blue;

    static final int RED = 0x0000FFFF;
    static final int GREEN = 0x00FF00FF;
    static final int BLUE = 0x00FFFF00;
    
    public RGBPixel(short red, short green, short blue){
        this.red = red;
        RGB = (this.red << 16) | RGB;
        this.green = green;
        RGB = (this.green << 8) | RGB;
        this.blue = blue;
        RGB = this.blue | RGB;
    }

    public RGBPixel(RGBPixel pixel){
        RGB = pixel.RGB;
    }

    // public RGBPixel(YUVPixel pixel){

    // }

    short getRed(){
        short red = (short)(RGB >> 16);

        return red;
    }

    short getGreen(){
        short green = (short)((RGB >> 8) & 255);

        return green;
    }

    short getBlue(){
        short blue = (short)(RGB & 255);

        return blue;
    }

    void setRed(short red){
        this.red = red;
        RGB = RGB & RED;
        RGB = (this.red << 16) | RGB;
    }
    
    void setGreen(short green){
        this.green = green;
        RGB = RGB & GREEN;
        RGB = (this.green << 8) | RGB;
    }

    void setBlue(short blue){
        this.blue = blue;
        RGB = RGB & BLUE;
        RGB = this.blue | RGB;
    }

    int getRGB(){
        return RGB;
    }

    void setRGB(int value){
        RGB = value;
    }

    final void setRGB(short red, short green, short blue){
        setRed(red);
        setGreen(green);
        setBlue(blue);
    }
    
    @Override
    public String toString() {
        return (getRed() + " " + getGreen() + " " + getBlue());
    }
}