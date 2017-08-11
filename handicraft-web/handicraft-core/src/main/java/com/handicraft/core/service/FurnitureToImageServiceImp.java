package com.handicraft.core.service;

import com.handicraft.core.dto.FurnitureToImage;
import com.handicraft.core.repository.FurnitureToImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Created by 고승빈 on 2017-08-06.
 */
@Service
public class FurnitureToImageServiceImp implements FurnitureToImageService{

    @Autowired
    FurnitureToImageRepository furnitureToImageRepository;

    @Override
    public Page<FurnitureToImage> findFurniturToImagePerPage(PageRequest pageRequest) {
        return furnitureToImageRepository.findAll(pageRequest);
    }

    @Override
    public FurnitureToImage insertFurnitureToImageByFid(FurnitureToImage furnitureToImage)
    {
        return furnitureToImageRepository.save(furnitureToImage);
    }

    @Override
    public FurnitureToImage updateFurnitureToImageByFid(FurnitureToImage furnitureToImage) {

        if(!furnitureToImageRepository.exists(furnitureToImage.getFid())) return null;

        return furnitureToImageRepository.save(furnitureToImage);
    }

    @Override
    public Boolean deleteFurnitureToImageByFid(long f_id) {
        if(!furnitureToImageRepository.exists(f_id)) return false;

        furnitureToImageRepository.delete(f_id);
        return true;
    }

    @Override
    public FurnitureToImage findFurnitureToImageByFid(long f_id) {
        return furnitureToImageRepository.findOne(f_id);
    }
}
