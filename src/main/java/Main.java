import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        ListRand testListRand = new ListRand();

        testListRand.add("Data1");
        testListRand.add("Data2");
        testListRand.add("Data3");
        testListRand.add("Data4");
        testListRand.add("Data5");

        ListNode listNode_2 = testListRand.getAt(1);
        listNode_2.Rand = listNode_2;

        ListNode listNode_3 = testListRand.getAt(1);
        ListNode listNode_5 = testListRand.getAt(4);
        listNode_3.Rand = listNode_5;

        ListNode listNode_1 = testListRand.getAt(0);
        ListNode listNode_4 = testListRand.getAt(3);
        listNode_4.Rand = listNode_1;

        try (FileOutputStream fileOutputStream = new FileOutputStream("data.txt")) {
            testListRand.Serialize(fileOutputStream);
        }

        ListRand newTestListRand = new ListRand();
        try (FileInputStream fileInputStream = new FileInputStream("data.txt")) {
            ListRand deserialize = newTestListRand.Deserialize(fileInputStream);
            System.out.println(deserialize.toListNodeByIndex());
        }
    }
}

class ListNode {
    public ListNode Prev;
    public ListNode Next;
    public ListNode Rand; // произвольный элемент внутри списка
    public String Data;
}

class ListNodeTemp {
    private final Integer index;
    private final String data;
    private final Integer randIndex;

    public ListNodeTemp(Integer index, String data, Integer randIndex) {
        this.index = index;
        this.data = data;
        this.randIndex = randIndex;
    }

    public Integer getIndex() {
        return index;
    }

    public String getData() {
        return data;
    }

    public Integer getRandIndex() {
        return randIndex;
    }
}

class ListRand {
    public ListNode Head;
    public ListNode Tail;
    public int Count;

    public void add(String data) {
        ListNode newListNode = new ListNode();
        newListNode.Data = data;

        if (Head == null) {
            Head = newListNode;
            Tail = newListNode;
            Head.Next = Tail;
            Tail.Prev = Head;
        } else if (Head == Tail) {
            Head.Next = newListNode;
            newListNode.Prev = Head;
            Tail = newListNode;
        } else {
            ListNode lastTail = Tail;
            lastTail.Next = newListNode;
            newListNode.Prev = lastTail;
            Tail = newListNode;
        }

        Count++;
    }

    public ListNode getAt(int index) {
        if (index > Count || index < 0) throw new IndexOutOfBoundsException();
        int i = 0;
        ListNode listNode = Head;
        while (i < index) {
            listNode = listNode.Next;
            i++;
        }
        return listNode;
    }

    public void Serialize(FileOutputStream fileOutputStream) throws IOException {
        String delimiter = ":";
        String endLine = "\n";

        Map<ListNode, Integer> listNodeByIndex = toListNodeByIndex();
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<ListNode, Integer> listNodeIntegerEntry : listNodeByIndex.entrySet()) {
            ListNode listNode = listNodeIntegerEntry.getKey();
            String data = listNode.Data;
            Integer randIndex = listNodeByIndex.get(listNode.Rand);
            stringBuilder
                    .append(data)
                    .append(delimiter)
                    .append(randIndex)
                    .append(endLine);
        }

        fileOutputStream.write(stringBuilder.toString().getBytes());
    }

    public ListRand Deserialize(FileInputStream fileInputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        List<ListNodeTemp> nodeTemps = new ArrayList<>();

        int index = 0;
        while (reader.ready()) {
            String line = reader.readLine();
            String[] split = line.split(":");

            String data = split[0].equals("null") ? null : split[0];
            Integer randIndex = split[1].equals("null") ? null : Integer.parseInt(split[1]);

            nodeTemps.add(new ListNodeTemp(index, data, randIndex));
            index++;
        }

        for (ListNodeTemp nodeTemp : nodeTemps) {
            this.add(nodeTemp.getData());
        }

        ArrayList<ListNode> listNodes = new ArrayList<>(toListNodeByIndex().keySet());

        for (ListNodeTemp nodeTemp : nodeTemps) {
            Integer randIndex = nodeTemp.getRandIndex();

            if (nodeTemp.getRandIndex() == null) continue;

            Integer listNodeIndex = nodeTemp.getIndex();

            ListNode listNode = listNodes.get(listNodeIndex);
            listNode.Rand = listNodes.get(randIndex);
        }

        return this;
    }

    public Map<ListNode, Integer> toListNodeByIndex() {
        ListNode listNode = this.Head;
        Map<ListNode, Integer> listNodeByIndex = new LinkedHashMap<>();

        for (int i = 0; i < this.Count; i++) {
            listNodeByIndex.put(listNode, i);
            listNode = listNode.Next;
        }

        return listNodeByIndex;
    }
}

