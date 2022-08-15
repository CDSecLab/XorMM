package Client;

import Client.entity.KV;
import util.AESUtil;
import util.tool;
import util.Hash;
import util.GGM;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Cuckoo_Hash {
    private static long K_d=456;
    private static byte[] K_e = "7975922666f6eb02".getBytes(StandardCharsets.UTF_8);

    static int[] cuckoo_table;//store raw data
    static byte[][] EMM;//store ciphertext
    private static ArrayList<KV> stash = new ArrayList<KV>();//store evicted elements
    private static Map<String,Integer> leave_map = new HashMap<String,Integer>();
    public Cuckoo_Hash(){}

    public void setup(KV[] kv_list, int level) throws Exception {
        int table_size = (int) Math.floor((kv_list.length * (1 + 0.3)));
        cuckoo_table = new int[table_size*2];
        Arrays.fill(cuckoo_table,-1);
        EMM = new byte[table_size*2][32];

        for (int i = 0; i < kv_list.length; i++) {
            Cuckoo_Hash.insertEntry(i,kv_list,table_size,level);
        }

        Random random = new Random();
        for(int i=0;i<cuckoo_table.length;i++) {
            if (cuckoo_table[i] == -1) {
                cuckoo_table[i] = random.nextInt(1000000);
                EMM[i] = AESUtil.encrypt(K_e,("dummy_dummy_dum_"+cuckoo_table[i]).getBytes(StandardCharsets.UTF_8));
            }else{
                EMM[i]=AESUtil.encrypt(K_e,(kv_list[cuckoo_table[i]].key+","+kv_list[cuckoo_table[i]].value).getBytes(StandardCharsets.UTF_8));
            }

        }
    }

    public long Get_K_d(){
        return K_d;
    }

    public byte[] Get_K_e() { return K_e; }

    public byte[][] Get_EMM(){
        return EMM;
    }

    public ArrayList<KV> Get_Stash(){ return stash;}

    public void Leave_Map_Clear() { leave_map.clear();}

    static void insertEntry(int key,KV[] kv_list,int table_size,int level) throws Exception {
        int count = 0;
        int h =0;
        int Left_Node, Right_Node;
        while (count < 5*level) {
            String k = kv_list[key].key+","+kv_list[key].counter;
            String k0 = k+",0";
            if(leave_map.containsKey(k0)) {
                Left_Node = leave_map.get(k0);
            }else{
                byte[] father_Node = GGM.Doub_GGM_Path(Hash.Get_SHA_256((kv_list[key].key+K_d).getBytes(StandardCharsets.UTF_8)), level, tool.TtS(kv_list[key].counter, 2, level));
                Left_Node = GGM.Map2Range(Arrays.copyOfRange(father_Node, 1 , 9 ),table_size,0);
                leave_map.put(k0,Left_Node);
                Right_Node = GGM.Map2Range(Arrays.copyOfRange(father_Node, 17, 26),table_size,1);
                leave_map.put(k+",1",Right_Node);
            }
            h = Left_Node;
            int temp = cuckoo_table[h];
            if (temp == -1) {
                cuckoo_table[h] = key;
                return;
            } else {
                cuckoo_table[h] = key;
                key = temp;
            }

            k = kv_list[key].key+","+kv_list[key].counter;
            String k1 =  k+",1";
            Right_Node = leave_map.get(k1);
            h = Right_Node;
            temp = cuckoo_table[h];
            if (temp == -1) {
                cuckoo_table[h] = key;
                return;
            } else {
                cuckoo_table[h] = key;
                key = temp;
            }
            ++count;
        }
        System.out.println("add an element into the stash");
        stash.add(kv_list[key]);
    }
}

