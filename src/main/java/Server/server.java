package Server;

import util.GGM;
import util.tool;

import java.io.*;
import java.util.*;

public class server {
    private static byte[][] EMM;
    private static int MAX_VOLUME_LENGTH;
    private static int server_level;
    private static int server_DEFAULT_INITIAL_CAPACITY;
    private ArrayList<byte[]> C_key = new ArrayList<byte[]>();
    public server(){}


    public server(byte[][] fp,int volume_length, int level,int DEFAULT_INITIAL_CAPACITY){
        EMM = fp;
        MAX_VOLUME_LENGTH = volume_length;
        server_level = level;
        server_DEFAULT_INITIAL_CAPACITY = DEFAULT_INITIAL_CAPACITY;
    }

    public void  Query_Xor(byte[] hash){
        for (int i = 0;i<MAX_VOLUME_LENGTH;i++ ) {
                byte[] father_Node = GGM.Tri_GGM_Path(hash, server_level, tool.TtS(i, 3, server_level));
                int t0 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 1 , 9),server_DEFAULT_INITIAL_CAPACITY,0);
                int t1 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 11, 19),server_DEFAULT_INITIAL_CAPACITY,1);
                int t2 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 21, 29),server_DEFAULT_INITIAL_CAPACITY,2);
                byte[] res = tool.Xor(tool.Xor(EMM[t0], EMM[t1]), EMM[t2]);
                C_key.add(res);
            }

    }

    public void  Query_Cuckoo(byte[] hash){
        //GGM.clear();
        for (int i = 0;i<MAX_VOLUME_LENGTH;i++ ) {
            byte[] father_Node = GGM.Doub_GGM_Path(hash, server_level, tool.TtS(i, 2, server_level));
            int t0 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 1 , 9),server_DEFAULT_INITIAL_CAPACITY,0);
            int t1 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 17, 26),server_DEFAULT_INITIAL_CAPACITY,1);
            C_key.add(EMM[t0]);
            C_key.add(EMM[t1]);
        }
    }
    public ArrayList<byte[]> Get_C_key(){ return C_key; }
    public void Clear(){ C_key.clear();}

    public static void Store_Server(String text) {
        try {
            FileOutputStream file = new FileOutputStream("Server_"+text+".dat");
            for (int i = 0; i < EMM.length; i++) {
                file.write(EMM[i]);
            }
            file.close();
        } catch (IOException e) {
            System.out.println("Error - " + e.toString());
        }
    }



}
