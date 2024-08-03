import java.util.HashMap;
import java.util.ArrayList;


public class TaskManager {
    static int id = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    public void addTask(Task aTask){
        int newId = id++;

        aTask.setId(newId);
        tasks.put(newId, aTask);
    }

    public void taskUpdate(Task updatedTask){
        int updatedTaskId = updatedTask.getId();

        if(tasks.containsKey(updatedTaskId)){
            tasks.put(updatedTaskId, updatedTask);
        }
    }

    public Task getTaskById(int taskId){
        if (tasks.containsKey(taskId)){
            return tasks.get(taskId);
        } else{
            return null;
        }
    }

    public HashMap getAllTasks(){
        return tasks;
    }

    public void removeAllTasks(){
        tasks.clear();
    }

    public void removeTaskById(int id){
        if (tasks.containsKey(id)){
            tasks.remove(id);
        }
    }

    public void addEpic(Epic anEpic){
        int newId = id++;

        anEpic.setId(newId);
        epics.put(newId, anEpic);
    }

    public void epicUpdate(Epic updatedEpic){
        int updatedEpicId = updatedEpic.getId();

        if(epics.containsKey(updatedEpicId)){
            ArrayList<Integer> updatedSubtaskIds = epics.get(updatedEpicId).getSubtaskIds();

            epics.put(updatedEpicId, updatedEpic);
            epics.get(updatedEpicId).setSubtaskIds(updatedSubtaskIds);
            changeEpicStatus(updatedEpicId);
        }
    }

    public Epic getEpicById(int epicId){
        if (epics.containsKey(epicId)){
            return epics.get(epicId);
        } else{
            return null;
        }
    }

    public HashMap getAllEpics(){
        return epics;
    }

    public void removeAllEpics(){
        epics.clear();
        removeAllSubtask();
    }

    public void removeEpicById(int id){
        if(epics.containsKey(id)){
            for(int subtaskId : epics.get(id).subtaskIds){
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public void addSubtask(Subtask aSubtask){
        if (subtasks.containsKey(aSubtask.getId())){
            return;
        }

        int epicId = aSubtask.getEpicId();

        if (!epics.containsKey(epicId)){
            return;
        }

        int newId = id++;

        aSubtask.setId(newId);
        subtasks.put(newId, aSubtask);

        Epic subtasksEpic = epics.get(epicId);

        subtasksEpic.subtaskIds.add(aSubtask.getId());
        changeEpicStatus(epicId);
    }

    public void subtaskUpdate(Subtask updatedSubtask){
        int updatedSubtaskId = updatedSubtask.getId();
        int epicId = updatedSubtask.getEpicId();

        if(subtasks.containsKey(updatedSubtaskId)){
            subtasks.put(updatedSubtaskId, updatedSubtask);
            changeEpicStatus(epicId);
        }
    }

    public Task getSubtaskById(int subtaskId){
        if (subtasks.containsKey(subtaskId)){
            return subtasks.get(subtaskId);
        } else{
            return null;
        }
    }

    public HashMap getAllSubtasks(){
        return subtasks;
    }

    public ArrayList<Integer> getAllEpicSubtasks(int epicId){
        ArrayList<Integer> epicSubtasksIds = epics.get(epicId).getSubtaskIds();
        return epicSubtasksIds;
    }

    public void removeAllSubtask(){
        subtasks.clear();
    }

    public void removeSubtaskById(int id){
        if (subtasks.containsKey(id)){
            int epicId = subtasks.get(id). getEpicId();

            subtasks.remove(id);
            epics.get(epicId).subtaskIds.remove(Integer.valueOf(id));
            changeEpicStatus(epicId);
        }
    }

    private void changeEpicStatus(int epicId){
        Epic epicToChangeStatus = epics.get(epicId);
        ArrayList<Integer> subtaskIdS = epicToChangeStatus.getSubtaskIds();
        int amountOfDoneSubtusks = 0;
        int amountOfNewSubtusks = 0;

        if(!subtaskIdS.isEmpty()){
            for(int subtaskId : subtaskIdS){
                TaskStatus subtaskStatus = subtasks.get(subtaskId).getTaskStatus();

                // if (subtaskStatus == TaskStatus.IN_PROGRESS){
                //     epicToChangeStatus.setTaskStatus(TaskStatus.IN_PROGRESS);
                //     return;
                // } else
                if (subtaskStatus == TaskStatus.DONE){
                    amountOfDoneSubtusks++;
                } else if (subtaskStatus == TaskStatus.NEW){
                    amountOfNewSubtusks++;
                }
            }
            if(amountOfDoneSubtusks == subtaskIdS.size()){
                epicToChangeStatus.setTaskStatus(TaskStatus.DONE);
            } else if(amountOfNewSubtusks != subtaskIdS.size()){
                epicToChangeStatus.setTaskStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}
