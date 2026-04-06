package com.mycompany.hotelmanagementsystem.dao;

import com.mycompany.hotelmanagementsystem.model.RoomImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RoomImageRepository extends BaseRepository<RoomImage> {

    @Override
    protected RoomImage mapRow(ResultSet rs) throws SQLException {
        RoomImage img = new RoomImage();
        img.setImageId(rs.getInt("image_id"));
        img.setTypeId(rs.getInt("type_id"));
        img.setImageUrl(rs.getString("image_url"));
        return img;
    }

    public List<RoomImage> findByTypeId(int typeId) {
        String sql = "SELECT * FROM RoomImage WHERE type_id = ? ORDER BY image_id";
        return queryList(sql, typeId);
    }

    public RoomImage findFirstByTypeId(int typeId) {
        String sql = "SELECT TOP 1 * FROM RoomImage WHERE type_id = ? ORDER BY image_id";
        return queryOne(sql, typeId);
    }
}
