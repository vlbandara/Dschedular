package workload;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;

public class Task extends Cloudlet {

    private String type; // e.g., CPU, IO, Memory

    public Task(int id, long length, int pesNumber, long fileSize, long outputSize, String type) {
        super(id, length, pesNumber, fileSize, outputSize, new UtilizationModelFull(), new UtilizationModelFull());
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
