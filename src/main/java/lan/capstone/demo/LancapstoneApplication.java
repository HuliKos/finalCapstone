package lan.capstone.demo;

import lan.capstone.demo.model.Contract;
import lan.capstone.demo.model.LoginAttempt;
import lan.capstone.demo.model.ChangePassAttempt;
import lan.capstone.demo.model.Task;
import lan.capstone.demo.model.User;
import lan.capstone.demo.repository.ContractRepository;
import lan.capstone.demo.repository.TaskRepository;
import lan.capstone.demo.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@Controller
public class LancapstoneApplication {

	@Autowired
	UserRepository userRepository;

	@Autowired
	ContractRepository contractRepository;

	@Autowired
	TaskRepository taskRepository;

	public static void main(String[] args) {
		SpringApplication.run(LancapstoneApplication.class, args);
	}

	// Admin debug page.
	@GetMapping("/admin")
	public String adminLandingPage(Model model) {
		String userListPrint = "Current List of Users:\n";
		String contractListPrint = "Current List of Contracts:\n";
		String taskListPrint = "Current List of Tasks:\n";

		userRepository.deleteAll();
		contractRepository.deleteAll();
		taskRepository.deleteAll();

		// save a couple of users.
		User user1 = userRepository.save(new User("lankos", "Lan", "Kostrikin", "lan@gmail.com", "123456789","password123"));
		User user2 = userRepository.save(new User("bobo", "Bob", "Odenkirk", "bob@gmail.com", "123456789","bob123"));
		User user3 = userRepository.save(new User("davec", "Dave", "Chapelle", "davec@gmail.com", "123456789","dave234"));

		// save a couple of contracts.
		Contract contract1 = contractRepository.save(new Contract("Lan's first contract", "100k", "01/01/2023", "02/01/2023", user1));
		Contract contract2 = contractRepository.save(new Contract("Lan's second contract", "50k", "02/01/2023", "03/01/2023", user1));
		Contract contract3 = contractRepository.save(new Contract("Bob's last contract", "1k", "12/01/2022", "01/01/2023", user2));

		// save a couple of tasks.
		taskRepository.save(new Task("Eat", true, contract1));
		taskRepository.save(new Task("Sleep", true, contract1));
		taskRepository.save(new Task("Work", true, contract1));

		taskRepository.save(new Task("Clean", true, contract2));
		taskRepository.save(new Task("Exercise", true, contract2));

		taskRepository.save(new Task("Act", true, contract3));

		for (User webUser : userRepository.findAll()) {
			userListPrint = userListPrint + webUser.toString() + "\n";
		}

		for (Contract contract : contractRepository.findAll()) {
			contractListPrint = contractListPrint + contract.toString() + "\n";
		}

		for (Task task : taskRepository.findAll()) {
			taskListPrint = taskListPrint + task.toString() + "\n";
		}

		model.addAttribute("var1", userListPrint);
		model.addAttribute("var2", contractListPrint);
		model.addAttribute("var3", taskListPrint);

		return "blank";
	}

	// Index page with login form.
	@GetMapping("/")
	public String login(Model model) {
		// Empty User model object to store form data
		LoginAttempt loginAttempt = new LoginAttempt();
		model.addAttribute("loginAttempt", loginAttempt);
		return "index";
	}

	// Login result page. Redirects to dashboard if success, otherwise prompts error.
	@PostMapping("/login/result")
	public String loginSubmit(Model model, @ModelAttribute("loginAttempt") LoginAttempt loginAttempt) {
		User _user = userRepository.findByUsername(loginAttempt.getUsername());
		if (_user != null && _user.getPassword().equals(loginAttempt.getPassword())) {
			model.addAttribute("loginAttempt", loginAttempt);
			return "redirect:/dashboard/" + loginAttempt.getUsername();
		}
		else {
			model.addAttribute("msg", "Invalid Credentials.");
			return "index";
		}
	}

	// User dashboard page.
	@GetMapping("/dashboard/{username}")
	public String dashboard(@PathVariable("username") String username, Model model) {
		User user = userRepository.findByUsername(username);
		List<Contract> contracts = contractRepository.findByUser(user);

		List<Task> tasks = new ArrayList<>();
		for (Contract contract: contracts) {
			tasks.addAll(taskRepository.findByContract(contract));
		}

		model.addAttribute("user", user);
		model.addAttribute("contracts", contracts);
		model.addAttribute("tasks", tasks);
		return "user-dashboard";
	}

	// User registration page.
	@GetMapping("/register")
	public String register(Model model) {
		// Empty User model object to store form data
		User user = new User();
		model.addAttribute("user", user);
		return "user-register";
	}

	// User registration results page.
	@PostMapping("/register/save")
	public String registerSubmit(Model model, @ModelAttribute("user") User user) {
		User _user = userRepository.findByUsername(user.getUsername());
		if (_user != null) {
			model.addAttribute("msg", "Username Already Taken.");
			return "redirect:/register";
		}
		else {
			userRepository.save(user);
			model.addAttribute("user", user);
			return "user-register-success";
		}
	}

	// User change password page.
	@GetMapping("/dashboard/{username}/chpass")
	public String changePassword(@PathVariable("username") String username, Model model) {
		// Empty ChangePassAttempt model object to store form data
		ChangePassAttempt changePassAttempt = new ChangePassAttempt();
		model.addAttribute("username", username);
		model.addAttribute("changePassAttempt", changePassAttempt);
		return "change-password";
	}

	// User change password result page.
	@PostMapping("/dashboard/{username}/chpass-result")
	public String changePasswordSubmit(@PathVariable("username") String username, Model model,
									   @ModelAttribute("changePassAttempt") ChangePassAttempt changePassAttempt,
									   RedirectAttributes redirectAttributes) {
		User _user = userRepository.findByUsername(changePassAttempt.getUsername());
		if (_user != null &&
				_user.getPassword().equals(changePassAttempt.getCurpass()) &&
				changePassAttempt.getNewpass().equals(changePassAttempt.getNewpassconf())
		) {
			_user.setPassword(changePassAttempt.getNewpass());
			userRepository.save(_user);
			redirectAttributes.addFlashAttribute("msg", "Password Updated Successfully!");
			return "redirect:/dashboard/" + changePassAttempt.getUsername();
		}
		else {
			model.addAttribute("msg", "Invalid Credentials.");
			return "change-password";
		}
	}

	// Delete user by id.
	@RequestMapping(value = "/dashboard/{username}/delete", method={RequestMethod.DELETE, RequestMethod.GET})
	@Transactional
	public String deleteUser(@PathVariable("username") String username, Model model,
							 RedirectAttributes redirectAttributes) {

		long count = userRepository.deleteByUsername(username);

		if (count > 0) {
			redirectAttributes.addFlashAttribute("msg", "Account Deleted.");
		}
		else {
			redirectAttributes.addFlashAttribute("msg", "Unable to Delete Account.");
		}

		return "redirect:/";
	}

	// User add Contract result page.
	@GetMapping("/{username}/add-contract")
	public String addContract(@PathVariable("username") String username, Model model) {
		// Empty Contract model object to store form data
		Contract contract = new Contract();
		model.addAttribute("contract", contract);
		model.addAttribute("username", username);
		return "add-contract";
	}

	// User add Contract result page.
	@PostMapping("/{username}/add-contract-result")
	public String addContractSubmit(@PathVariable("username") String username, Model model,
									@ModelAttribute("contract") Contract contract,
									RedirectAttributes redirectAttributes) {
		User _user = userRepository.findByUsername(username);

		if (_user != null) {
			contract.setUser(_user);
			contractRepository.save(contract);
			redirectAttributes.addFlashAttribute("msg", "Contract Created Successfully!");
		}
		else {
			redirectAttributes.addFlashAttribute("msg", "There was a problem creating the " +
					"Contract!");
		}

		return "redirect:/dashboard/" + _user.getUsername();
	}

	// User Add task page.
	@GetMapping("/{username}/contract/{contract_id}/add-task")
	public String addTask(@PathVariable("username") String username,
						  @PathVariable("contract_id") long contract_id, Model model) {
		// Empty Task model object to store form data
		Task task = new Task();
		model.addAttribute("task", task);
		model.addAttribute("username", username);
		model.addAttribute("contract_id", contract_id);
		return "add-task";
	}

	// User Add task result page.
	@PostMapping("/{username}/contract/{contract_id}/add-task-result")
	public String addTaskSubmit(Model model,
								@ModelAttribute("task") Task task,
								@PathVariable("username") String username,
								@PathVariable("contract_id") long contract_id,
								RedirectAttributes redirectAttributes) {
		Contract _contract = contractRepository.findById(contract_id);

		if (_contract != null) {
			task.setStatus(true);
			task.setContract(_contract);
			taskRepository.save(task);
			redirectAttributes.addFlashAttribute("msg", "Task Created Successfully!");
			return "redirect:/dashboard/" + username;
		}
		else {
			redirectAttributes.addFlashAttribute("msg", "There was a system error creating " +
					"the Task!");
			return "redirect:/";
		}

	}

	// User complete task page.
	@GetMapping("/{username}/task/{task_id}/complete")
	public String completeTask(Model model,
								@PathVariable("username") String username,
								@PathVariable("task_id") long task_id,
								RedirectAttributes redirectAttributes) {
		Task _task = taskRepository.findById(task_id);

		if (_task != null) {
			_task.setStatus(false);
			taskRepository.save(_task);
			redirectAttributes.addFlashAttribute("msg", "Task Marked as Completed!");
			return "redirect:/dashboard/" + username;
		}
		else {
			redirectAttributes.addFlashAttribute("msg", "There was a system error completing " +
					"the Task!");
			return "redirect:/";
		}

	}

}