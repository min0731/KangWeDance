package com.ssafy.kang.photos.model.mapper;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.kang.photos.model.FramesDto;
import com.ssafy.kang.photos.model.PhotosDto;

@Mapper
public interface PhotosMapper {

//	| selectOrder() | 조회 유형의 mapper 메서드 |
//	| insertOrder() | 등록 유형의 mapper 메서드 |
//	| updateOrder() | 변경 유형의 mapper 메서드 |
//	| deleteOrder() | 삭제 유형의 mapper 메서드 |

	public void insertPhoto(PhotosDto photosDto);

	public List<PhotosDto> selectPhotos(int parentIdx, int pageNum) throws SQLException;

	public List<FramesDto> selectFrames(int level) throws SQLException;

	public boolean deletePhoto(int photoIdx) throws SQLException;

	public int selectPhotosCount(int parentIdx) throws SQLException;

	public int selectPramesCount(int level) throws SQLException;

	public int selectLevel(int parentIdx) throws SQLException;

	public List<FramesDto> selectStickers() throws SQLException;

	// 카카오 공유하기 -> 나중

}
