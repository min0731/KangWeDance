package com.ssafy.kang.hive.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.kang.children.model.BodyRecordForHadoop;
import com.ssafy.kang.children.model.mapper.ChildrenMapper;
import com.ssafy.kang.common.ErrorCode;
import com.ssafy.kang.common.SuccessCode;
import com.ssafy.kang.common.dto.ApiResponse;
import com.ssafy.kang.play.model.PlayRecordForHadoop;
import com.ssafy.kang.play.model.service.PlayService;

@RestController
@RequestMapping("/hive")
public class HiveController {

	@Autowired
	@Qualifier("jdbcHiveTemplate")
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ChildrenMapper childrenMapper;

	@Autowired
	PlayService playService;

	@GetMapping("/tables")
	public ApiResponse<?> showDatabases() {
		List<Map<String, Object>> row = null;
		System.out.println(1);
		row = jdbcTemplate.queryForList("show tables");
		return ApiResponse.success(SuccessCode.CREATE_BODY_CHILDREN, row);
	}

	@GetMapping("/body_record")
	public ApiResponse<?> bodyRecord() {
		// RDB에서 바디 레코드 가져오는 코드
		List<BodyRecordForHadoop> bodyRecordDto = null;
		try {
			bodyRecordDto = childrenMapper.selectBodyRecordForHadoop();
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponse.error(ErrorCode.INTERNAL_SERVER_EXCEPTION);
		}

		int childIdx;
		double weight; // 몸무게
		double bmi; // BMI
		double height; // 키
		double todayCalories; // 오늘 칼로리 소모량
		double bodyRecordIdx; // 권장 칼로리량
		String recordDate; // 기록 날짜

		for (int i = 0; i < bodyRecordDto.size(); i++) {
			childIdx = bodyRecordDto.get(i).getChildIdx();
			weight = bodyRecordDto.get(i).getWeight();
			height = bodyRecordDto.get(i).getHeight();
			todayCalories = bodyRecordDto.get(i).getTodayCalrories();
			bodyRecordIdx = bodyRecordDto.get(i).getBodyRecordIdx();
			recordDate = bodyRecordDto.get(i).getRecordDate();
			bmi = bodyRecordDto.get(i).getBmi();

			// 하둡에 insert하는 코드
			int result = jdbcTemplate.update("insert into table bodyRecord values (" + bodyRecordIdx + "," + childIdx
					+ "," + weight + "," + height + "," + bmi + "," + todayCalories + ",'" + recordDate + "')");
		}
		return ApiResponse.success(SuccessCode.CREATE_BODYRECORD);
	}

	// playRecord insert API
	@GetMapping("/play_record")
	public ApiResponse<?> playRecord() throws Exception {

		List<PlayRecordForHadoop> playRecordDto = playService.findplayRecordForHadoop();

		String recordDate;
		int playrecordIdx;
		int childIdx;
		int songIdx;
		int playMode;

		int result = 0;

		for (int i = 0; i < playRecordDto.size(); i++) {
			recordDate = playRecordDto.get(i).getRecordDate();
			playrecordIdx = playRecordDto.get(i).getPlayrecordIdx();
			childIdx = playRecordDto.get(i).getChildIdx();
			songIdx = playRecordDto.get(i).getSongIdx();
			playMode = playRecordDto.get(i).getPlayMode();

			// System.out.println(playrecordIdx);
			result = jdbcTemplate.update("insert into table playRecord values (" + playrecordIdx + "," + childIdx + ","
					+ songIdx + "," + playMode + ",'" + recordDate + "')");
		}
		return ApiResponse.success(SuccessCode.CREATE_PLAYRECORD);

	}
}
// insert into table bodyRecord values (1, 1, 30, 140, 22.3, 0, '2023-03-24');
