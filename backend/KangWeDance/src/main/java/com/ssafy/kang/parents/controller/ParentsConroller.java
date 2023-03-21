package com.ssafy.kang.parents.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.AmazonS3Client;
import com.ssafy.kang.common.ErrorCode;
import com.ssafy.kang.common.SuccessCode;
import com.ssafy.kang.common.dto.ApiResponse;
import com.ssafy.kang.parents.model.ParentsDto;
import com.ssafy.kang.parents.model.service.ParentsService;
import com.ssafy.kang.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = { "*" }, maxAge = 6000)
@RequestMapping("/parents")
@RestController
public class ParentsConroller {
	@Autowired
	ParentsService parentsService;
	private final JwtUtil jwtUtil;
	public ParentsConroller() {
		this.jwtUtil = new JwtUtil();
	}
	@GetMapping("/social/kakao")
	public ApiResponse<?> kakaoUserAdd(@RequestParam String code){

		Map<String, String> token;
		Map<String, String> userIO;
		ParentsDto dto = new ParentsDto();
		try {
			token = parentsService.getToken(code);
			dto.setAccessToken(token.get("access_token"));
			userIO = parentsService.getUserInfo(dto.getAccessToken());
			dto.setSocailUid(userIO.get("id"));
			dto.setSocialPlatform("Kakao");
			dto.setNickname(userIO.get("nickname"));
			dto = parentsService.findSocial(dto.getSocailUid());
			return login(dto,userIO,token);
		} catch (Exception e) {
			return ApiResponse.error(ErrorCode.INTERNAL_SERVER_EXCEPTION);
		}
	}
	@GetMapping("/social/naver")
	public ApiResponse<?> NaverUserAdd(@RequestParam String code){

		Map<String, String> token;
		Map<String, String> userIO;
		ParentsDto dto = new ParentsDto();
		try {
			token = parentsService.getNaverToken(code);
			userIO = parentsService.getNaverUserInfo(token.get("access_token"));
			dto.setSocailUid(userIO.get("id"));
			dto.setSocialPlatform("Naver");
			dto.setNickname(userIO.get("nickname"));
			dto = parentsService.findSocial(dto.getSocailUid());
			return login(dto,userIO,token);
		} catch (Exception e) {
			return ApiResponse.error(ErrorCode.INTERNAL_SERVER_EXCEPTION);
		}
	}
	public ApiResponse<?> login(ParentsDto dto,Map<String, String> userIO,Map<String, String> token) throws Exception{
		Map<String, String> map = new HashMap<>();
		String accessToken ="";
		String isUser = "false";
		SuccessCode sc= null;
		if(dto != null && !dto.isDeletedFlag()) {
			dto.setAccessToken(token.get("access_token"));
			accessToken = jwtUtil.createAccessToken("useridx", dto.getParentIdx());
			parentsService.modifyAccessToken(dto);
			if(parentsService.findChildren(dto.getParentIdx())!=0)
				isUser = "true";
			sc = SuccessCode.LOGIN;
		}else {
			sc = SuccessCode.GO_JOIN;
			if(dto==null) {
				dto = new ParentsDto();		
				dto.setAccessToken(token.get("access_token"));
				dto.setSocailUid(userIO.get("id"));
				dto.setSocialPlatform("Kakao");
				dto.setNickname(userIO.get("nickname"));					
				accessToken =  jwtUtil.createAccessToken("useridx",parentsService.addUser(dto));
			}else {
				parentsService.modifyUser(dto);
			}
		}
		map.put("accessToken", accessToken);
		map.put("isUser", isUser);
		return ApiResponse.success(sc,map);
	}
	@PatchMapping("nickname")
	public ApiResponse<?> nicknameModify(@RequestParam String accesstoken, @RequestBody String nickname){
		try {
			
			parentsService.modifyNickname(ParentsDto.builder().parentIdx(jwtUtil.getUserIdx(accesstoken)).nickname(nickname).build());
			return ApiResponse.success(SuccessCode.UPDATE_NICKNAME);
		} catch (Exception e) {
			return ApiResponse.error(ErrorCode.INTERNAL_SERVER_EXCEPTION);
		}
		
	}
	@GetMapping("/experience-score")
	public ApiResponse<?> experienceDetails(@RequestParam String accesstoken){//추후 엑세스 토큰으로 대체
		try {
			return ApiResponse.success(SuccessCode.READ_EXPERIENCE,parentsService.findExperience(jwtUtil.getUserIdx(accesstoken)));
		} catch (Exception e) {
			return ApiResponse.error(ErrorCode.INTERNAL_SERVER_EXCEPTION);
		}
	}
	@DeleteMapping
	public ApiResponse<?> userRemove(@RequestParam String accesstoken){
		try {
			parentsService.removeUser(jwtUtil.getUserIdx(accesstoken));
			return ApiResponse.success(SuccessCode.DELETE_USER);
		} catch (Exception e) {
			return ApiResponse.error(ErrorCode.INTERNAL_SERVER_EXCEPTION);
		}
	}
	@GetMapping("logout")
	public ApiResponse<?> logout(@RequestParam String accesstoken){
		try {
			ParentsDto dto = parentsService.findUser(jwtUtil.getUserIdx(accesstoken));
			if(dto.getSocialPlatform().equals("Kakao")) {
				parentsService.kakaoLogout(dto.getAccessToken());
			}else if(dto.getSocialPlatform().equals("Naver")) {
				
			}
			return ApiResponse.success(SuccessCode.LOGOUT);
		} catch (Exception e) {
			return ApiResponse.error(ErrorCode.INTERNAL_SERVER_EXCEPTION);
		}
	}
}
