import java.util.ArrayList;

public class Epic extends Task{
    ArrayList<Integer> subtaskIds;

    public Epic(String epicName, String epicDiscr) {
        super(epicName, epicDiscr, TaskStatus.NEW);
        subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String epicName, String epicDiscr){
        super(id, epicName, epicDiscr, TaskStatus.NEW);
        subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }
}
