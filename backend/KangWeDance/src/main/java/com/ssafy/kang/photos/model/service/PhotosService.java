package com.ssafy.kang.photos.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.kang.photos.model.FramesDto;
import com.ssafy.kang.photos.model.PhotosDto;

@Service
public interface PhotosService {

//	| findOrder() | 조회 유형의 service 메서드 |
//	| addOrder() | 등록 유형의 service 메서드 |
//	| modifyOrder() | 변경 유형의 service 메서드 |
//	| removeOrder() | 삭제 유형의 service 메서드 |
//	| saveOrder() | 등록/수정/삭제 가 동시에 일어나는 유형의 service 메서드 |

	public void addUpdate(PhotosDto photosDto) throws Exception;

	public List<PhotosDto> findPhotos(int parentIdx, int pageNum) throws Exception;

	public List<FramesDto> findFrames(int level) throws Exception;

	public boolean removePhoto(int photoIdx) throws Exception;

	public int findPhotosCount(int parentIdx) throws Exception;

	public int findPramesCount(int level) throws Exception;

	public int findLevel(int parentIdx) throws Exception;

	public List<FramesDto> findStickers() throws Exception;

}
