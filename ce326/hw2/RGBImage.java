/**
 * RGBImage
 */
public class RGBImage implements Image{

    private int width;
    private int height;
    private int colordepth;
    RGBPixel[][] img;
    
    public RGBImage(int width, int height, int colordepth){
        this.width = width;
        this.height = height;
        this.colordepth = colordepth;

        RGBPixel[][] img = new RGBPixel[height][width];
    }

    public RGBImage(RGBImage copyImg){
        width = copyImg.width;
        height = copyImg.height;
        colordepth = copyImg.colordepth;
    }

    // public RGBImage(YUVImage YUVImg){

    // }

    int getWidth(){
        return width;
    }

    int getHeight(){
        return height;
    }

    int getColorDepth(){
        return colordepth;
    }

    RGBPixel getPixel(int row, int col){
        return (img[row][col]);
    }

    void setPixel(int row, int col, RGBPixel pixel){
        img[row][col] = pixel;
    }

    public void grayscale(){

    }

    public void doublesize(){

    }
    
    public void halfsize(){

    }

    public void rotateClockwise(){

    }
}