package Server;

import util.GGM;
import util.Hash;
import util.tool;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class server_proof {
    private static byte[][] EMM;
    private static byte[][] VMM;
    private static int MAX_VOLUME_LENGTH;
    private static int server_level;
    private static int server_DEFAULT_INITIAL_CAPACITY;
    private ArrayList<byte[]> C_key = new ArrayList<byte[]>();
    private byte[] P_key = new byte[32];

    public server_proof(){}

    public server_proof(byte[][] fp, byte[][] pf,int volume_length, int level,int DEFAULT_INITIAL_CAPACITY){
        EMM = fp;
        VMM = pf;
        MAX_VOLUME_LENGTH = volume_length;
        server_level = level;
        server_DEFAULT_INITIAL_CAPACITY = DEFAULT_INITIAL_CAPACITY;
    }




    public void  Query(byte[] hash){
        for (int i = 0;i<MAX_VOLUME_LENGTH;i++ ) {
            byte[] father_Node = GGM.Tri_GGM_Path(hash, server_level, tool.TtS(i, 3, server_level));
            int t0 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 1 , 9),server_DEFAULT_INITIAL_CAPACITY,0);
            int t1 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 11, 19),server_DEFAULT_INITIAL_CAPACITY,1);
            int t2 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 21, 29),server_DEFAULT_INITIAL_CAPACITY,2);
            byte[] res = tool.Xor(tool.Xor(EMM[t0], EMM[t1]), EMM[t2]);
            C_key.add(res);
            byte[] p_1 = tool.Xor(tool.Xor(VMM[t0], VMM[t1]), VMM[t2]);
            P_key = tool.Xor(P_key,Hash.Get_SHA_256(p_1));
        }
    }

    public ArrayList<byte[]> Get_C_key(){ return C_key; }

    public byte[] Get_P_key(){ return P_key; }
    public void Clear(){C_key.clear(); P_key = new byte[32];}


    public static void Store_Server_Proof(String text)  {
        try {
            FileOutputStream file = new FileOutputStream("Server_"+text+".dat");
            for (int i = 0; i < EMM.length; i++)
                file.write(EMM[i]);
            for (int i = 0; i < VMM.length; i++)
                file.write(VMM[i]);
            file.close();
        } catch (IOException e) {
            System.out.println("Error - " + e.toString());
        }
    }
}
