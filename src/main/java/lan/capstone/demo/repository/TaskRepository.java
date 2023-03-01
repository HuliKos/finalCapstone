package lan.capstone.demo.repository;

import lan.capstone.demo.model.Contract;
import lan.capstone.demo.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByName(String name);
    List<Task> findByStatus(Boolean status);
    List<Task> findByContract(Contract contract);
    Task findById(long task_id);

}
