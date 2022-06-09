
package ReconocimientoFacial;

import java.io.File;
import java.nio.IntBuffer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.javacpp.opencv_imgcodecs;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_GRAYSCALE;


public class Reconocer {

    private LBPHFaceRecognizer lbphRecognizer;
    
    public Reconocer() {
    }
    
    public void entrenarImagenes(){
        
        File Data = new File("data");
        File[] archivos = Data.listFiles();
        int contador1, contador2;
        contador1 = 0;
        
        for (File archivo : archivos) {
            File[] Personimage = archivo.listFiles();
            for (File image : Personimage) {
                contador1++;
            }
        }

        MatVector images = new MatVector(contador1);   
        Mat labels = new opencv_core.Mat(contador1, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();      
        
        contador2 = 0;
        contador1 = 0;
        for (File archivo : archivos) {
            
            File[] Personimage = archivo.listFiles();
            
            for (File image : Personimage) {
                opencv_core.Mat img = opencv_imgcodecs.imread(image.getAbsolutePath(), IMREAD_GRAYSCALE); // VARIABLE DE IMAGENES DE ALMACENAMIENTO
                org.bytedeco.javacpp.opencv_imgproc.equalizeHist(img, img); 
                images.put(contador1, img);
                labelsBuf.put(contador1, contador2);
                contador1++;
                
            }
            contador2++;
        }
        lbphRecognizer = LBPHFaceRecognizer.create();
        lbphRecognizer.train(images, labels);
    }
    
    public void loadDatabase(){
        lbphRecognizer = LBPHFaceRecognizer.create();
        lbphRecognizer.read("dataBase1.yml");   
    }
    
    public void saveDatabase(){
  
        lbphRecognizer.save("dataBase1.yml");


    }
    
    public int reconocer(){
 
        File rostros = new File("buscar\\buscar.png");
        Mat img = opencv_imgcodecs.imread(rostros.getAbsolutePath(), IMREAD_GRAYSCALE);
        org.bytedeco.javacpp.opencv_imgproc.equalizeHist(img, img);
        
        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);
        
        lbphRecognizer.predict(img, label, confidence);
        
        int predictedLabel = label.get(0);
        System.out.println(confidence.get(0)+"\t"+predictedLabel);

        if(confidence.get(0)>55){
            return -1;
        }
        
        return predictedLabel;
    
    }

  
}
