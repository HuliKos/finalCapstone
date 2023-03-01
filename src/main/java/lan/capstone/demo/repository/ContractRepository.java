package lan.capstone.demo.repository;

import lan.capstone.demo.model.Contract;
import lan.capstone.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract,Long> {
    List<Contract> findByUser(User user);
    Contract findById(long contract_id);

}

