package openglbasicconfig.leesg.com.openglbasicconfig;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LeeSG on 2015-08-24.
 */
public class Mesh {
    private static int mProgramImage;
    private int mPositionHandle;
    private int mTexCoordLoc;
    private int mtrxhandle;
    private int mSamplerLoc;
    private int mHandleBitmap;
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private FloatBuffer normBuffer;
    private FloatBuffer texBuffer;
    private int indexCount;
    //메인 액티비티
    MainActivity mActivity;
    public Mesh (int programImage, MainActivity mainActivity) {
        mProgramImage = programImage;
        mActivity = mainActivity;
        mPositionHandle = GLES20.glGetAttribLocation(mProgramImage, "vPosition");
        mTexCoordLoc = GLES20.glGetAttribLocation(mProgramImage, "a_texCoord");
        mtrxhandle = GLES20.glGetUniformLocation(mProgramImage, "uMVPMatrix");
        mSamplerLoc = GLES20.glGetUniformLocation(mProgramImage, "s_texture");
    }

    public void setBitmap(int handle) {
        mHandleBitmap = handle;
    }


    public void loadOBJ(String filename) {
        List<Float> vertices = new ArrayList();
        List<Float> normals = new ArrayList();
        List<Float> textures = new ArrayList();
        // obj파일의 v, vt, vn을 쭉 읽어서 temp에 저장한 뒤 f 를 읽으면서 위의 리스트에 저장한다.
        List<Float> temp_vertices = new ArrayList();
        List<Float> temp_normals = new ArrayList();
        List<Float> temp_textures = new ArrayList();
        List<Short> indices = new ArrayList();
        /*FileReader fr = null;
        try {
            fr = new FileReader(new File(filename + ".obj"));
            InputStream stream = getResources().openRawResource(R.raw.a);
        } catch(Exception e) {
            System.err.println("Impossible to find the obj file");
            e.printStackTrace();
        }
        if(fr == null)
        {
            System.err.println("error");
            return;
        }
        BufferedReader reader = new BufferedReader(fr);*/
        BufferedReader reader = mActivity.getAssetFile(filename + ".obj");
        String line;
        float[] vertexArray = null;
        float[] normalArray = null;
        float[] textureArray = null;
        int[] indexArray = null;
        int i = 0;
        try{
            while(true) {
                line = reader.readLine();
                String[] currentLine = line.split(" ");
                if(line.startsWith("v ")) {
                    temp_vertices.add(Float.parseFloat(currentLine[1]));
                    temp_vertices.add(Float.parseFloat(currentLine[2]));
                    temp_vertices.add(Float.parseFloat(currentLine[3]));
                    //Log.e("", currentLine[1] + " " + currentLine[2] + " " + currentLine[3]);
                } else if(line.startsWith("vt ")) {
                    temp_textures.add(Float.parseFloat(currentLine[1]));
                    temp_textures.add(Float.parseFloat(currentLine[2]));
                    //Log.e("", currentLine[1] + " " + currentLine[2]);// + " " + currentLine[3]);
                } else if(line.startsWith("vn ")) {
                    temp_normals.add(Float.parseFloat(currentLine[1]));
                    temp_normals.add(Float.parseFloat(currentLine[2]));
                    temp_normals.add(Float.parseFloat(currentLine[3]));
                    //Log.e("", currentLine[1] + " " + currentLine[2] + " " + currentLine[3]);
                } else if(line.startsWith("f ")) {
                    //vertexArray = new float[vertices.size()];
                    //textureArray = new float[textures.size()];
                    //normalArray = new float[normals.size()];
                    break;
                }
            }
            while(line!=null) {
                if(!line.startsWith("f ")) {
                    continue;
                }
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                //Log.e("", vertex1[0] + "/" + vertex1[1] + "/" + vertex1[2] + " " + vertex2[0] + "/" + vertex2[1] + "/" + vertex2[2] + " " + vertex3[0] + "/" + vertex3[1] + "/" + vertex3[2]);
                //vertex 1
                vertices.add(temp_vertices.get((Integer.parseInt(vertex1[0]) - 1) * 3 + 0));
                vertices.add(temp_vertices.get((Integer.parseInt(vertex1[0]) - 1) * 3 + 1));
                vertices.add(temp_vertices.get((Integer.parseInt(vertex1[0]) - 1) * 3 + 2));
                textures.add(temp_textures.get((Integer.parseInt(vertex1[1]) - 1) * 2 + 0));
                textures.add(temp_textures.get((Integer.parseInt(vertex1[1]) - 1) * 2 + 1));
                normals.add(temp_normals.get(Integer.parseInt(vertex1[2]) - 1) * 3 + 0);
                normals.add(temp_normals.get(Integer.parseInt(vertex1[2]) - 1) * 3 + 1);
                normals.add(temp_normals.get(Integer.parseInt(vertex1[2]) - 1) * 3 + 2);
                indices.add((short)i);
                i++;
                //vertex 2
                vertices.add(temp_vertices.get((Integer.parseInt(vertex2[0]) - 1) * 3 + 0));
                vertices.add(temp_vertices.get((Integer.parseInt(vertex2[0]) - 1) * 3 + 1));
                vertices.add(temp_vertices.get((Integer.parseInt(vertex2[0]) - 1) * 3 + 2));
                textures.add(temp_textures.get((Integer.parseInt(vertex2[1]) - 1) * 2 + 0));
                textures.add(temp_textures.get((Integer.parseInt(vertex2[1]) - 1) * 2 + 1));
                normals.add(temp_normals.get(Integer.parseInt(vertex2[2]) - 1) * 3 + 0);
                normals.add(temp_normals.get(Integer.parseInt(vertex2[2]) - 1) * 3 + 1);
                normals.add(temp_normals.get(Integer.parseInt(vertex2[2]) - 1) * 3 + 2);
                indices.add((short)i);
                i++;
                //vertex 3
                vertices.add(temp_vertices.get((Integer.parseInt(vertex3[0]) - 1) * 3 + 0));
                vertices.add(temp_vertices.get((Integer.parseInt(vertex3[0]) - 1) * 3 + 1));
                vertices.add(temp_vertices.get((Integer.parseInt(vertex3[0]) - 1) * 3 + 2));
                textures.add(temp_textures.get((Integer.parseInt(vertex3[1]) - 1) * 2 + 0));
                textures.add(temp_textures.get((Integer.parseInt(vertex3[1]) - 1) * 2 + 1));
                normals.add(temp_normals.get(Integer.parseInt(vertex3[2]) - 1) * 3 + 0);
                normals.add(temp_normals.get(Integer.parseInt(vertex3[2]) - 1) * 3 + 1);
                normals.add(temp_normals.get(Integer.parseInt(vertex3[2]) - 1) * 3 + 2);
                indices.add((short)i);
                i++;
                line = reader.readLine();
            }
            reader.close();
            indexCount = indices.size();
            ByteBuffer mVertices = ByteBuffer.allocateDirect(indexCount * 3 * 4);
            mVertices.order(ByteOrder.nativeOrder());
            vertexBuffer = mVertices.asFloatBuffer();
            ByteBuffer mNormals = ByteBuffer.allocateDirect(indexCount * 3 * 4);
            mNormals.order(ByteOrder.nativeOrder());
            normBuffer = mNormals.asFloatBuffer();
            ByteBuffer mTexCoords = ByteBuffer.allocateDirect(indexCount * 2 * 4);
            mTexCoords.order(ByteOrder.nativeOrder());
            texBuffer = mTexCoords.asFloatBuffer();
            ByteBuffer mIndices = ByteBuffer.allocateDirect(indexCount * 2);
            mIndices.order(ByteOrder.nativeOrder());
            indexBuffer = mIndices.asShortBuffer();
            //indexArray = new int[indexCount];
            for(i = 0; i < indexCount ; i++) {
                indexBuffer.put(i, indices.get(i));
                vertexBuffer.put(3 * i, vertices.get(3 * i));
                vertexBuffer.put(3 * i + 1, vertices.get(3 * i + 1));
                vertexBuffer.put(3 * i + 2, vertices.get(3 * i + 2));
                normBuffer.put(3 * i, normals.get(3 * i));
                normBuffer.put(3 * i + 1, normals.get(3 * i + 1));
                normBuffer.put(3 * i + 2, normals.get(3 * i + 2));
                texBuffer.put(2 * i, textures.get(2 * i));
                texBuffer.put(2 * i + 1, textures.get(2 * i + 1));

            }
        }catch (Exception e) {
            e.printStackTrace();
        }


        //vertexBuffer.put(vertexArray);
        //texBuffer.put(textureArray);
        //normBuffer.put(normalArray);
        //indexBuffer.put(indexArray);
        vertexBuffer.position(0);
        texBuffer.position(0);
        normBuffer.position(0);
        indexBuffer.position(0);

    }
    public void draw(float[] m) {
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, texBuffer);
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);
        // 투명한 배경을 처리한다.
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glDisable(GLES20.GL_BLEND);
        //이미지 핸들을 바인드 한다. 수정중
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mHandleBitmap);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }

}
