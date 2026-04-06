package com.mycompany.hotelmanagementsystem.controller.common;

<<<<<<< HEAD
import com.mycompany.hotelmanagementsystem.service.RoomService;
import com.mycompany.hotelmanagementsystem.model.Room;
import com.mycompany.hotelmanagementsystem.model.RoomType;
=======
import com.mycompany.hotelmanagementsystem.model.Promotion;
import com.mycompany.hotelmanagementsystem.model.Room;
import com.mycompany.hotelmanagementsystem.model.RoomType;
import com.mycompany.hotelmanagementsystem.service.PromotionService;
import com.mycompany.hotelmanagementsystem.service.RoomService;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
<<<<<<< HEAD
import java.util.List;
=======
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b

@WebServlet(urlPatterns = {"/rooms", "/rooms/detail"})
public class RoomController extends HttpServlet {
    private RoomService roomService;
<<<<<<< HEAD
=======
    private PromotionService promotionService;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b

    @Override
    public void init() {
        roomService = new RoomService();
<<<<<<< HEAD
=======
        promotionService = new PromotionService();
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/rooms/detail".equals(path)) {
            handleDetail(request, response);
        } else {
            handleList(request, response);
        }
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
<<<<<<< HEAD
        // Dùng inline params thay cho RoomSearchRequest
=======
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        Integer minPrice = parseIntParam(request, "minPrice");
        Integer maxPrice = parseIntParam(request, "maxPrice");
        Integer capacity = parseIntParam(request, "capacity");
        Integer typeId = parseIntParam(request, "typeId");

        boolean hasFilters = minPrice != null || maxPrice != null || capacity != null || typeId != null;

        List<RoomType> roomTypes;
        if (hasFilters) {
            roomTypes = roomService.searchRoomTypes(minPrice, maxPrice, capacity, typeId);
        } else {
            roomTypes = roomService.getAllRoomTypes();
        }

        List<RoomType> allTypes = roomService.getAllRoomTypes();

<<<<<<< HEAD
        request.setAttribute("roomTypes", roomTypes);
        request.setAttribute("allTypes", allTypes);
=======
        // Load active promotions and pre-computed discounted prices for all displayed room types
        Map<Integer, Promotion> promotionMap = new HashMap<>();
        Map<Integer, BigDecimal> discountedPriceMap = new HashMap<>();
        for (RoomType rt : roomTypes) {
            Promotion promo = promotionService.getActivePromotion(rt.getTypeId());
            if (promo != null) {
                promotionMap.put(rt.getTypeId(), promo);
                BigDecimal discounted = rt.getBasePrice()
                    .multiply(BigDecimal.valueOf(100).subtract(promo.getDiscountPercent()))
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                discountedPriceMap.put(rt.getTypeId(), discounted);
            }
        }

        request.setAttribute("roomTypes", roomTypes);
        request.setAttribute("allTypes", allTypes);
        request.setAttribute("promotionMap", promotionMap);
        request.setAttribute("discountedPriceMap", discountedPriceMap);
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        request.setAttribute("minPrice", minPrice);
        request.setAttribute("maxPrice", maxPrice);
        request.setAttribute("capacity", capacity);
        request.setAttribute("selectedTypeId", typeId);

        request.getRequestDispatcher("/WEB-INF/views/room/list.jsp").forward(request, response);
    }

    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer typeId = parseIntParam(request, "typeId");
        if (typeId == null) {
            response.sendRedirect(request.getContextPath() + "/rooms");
            return;
        }

        RoomType roomType = roomService.getRoomTypeById(typeId);
        if (roomType == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Room type not found");
            return;
        }

        int availableCount = roomService.getAvailableRoomCount(typeId);
        List<Room> availableRooms = roomService.getAvailableRooms(typeId, null, null);

<<<<<<< HEAD
=======
        // Load active promotion and pre-compute discounted price
        Promotion activePromo = promotionService.getActivePromotion(typeId);
        if (activePromo != null) {
            BigDecimal discountedPrice = roomType.getBasePrice()
                .multiply(BigDecimal.valueOf(100).subtract(activePromo.getDiscountPercent()))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
            request.setAttribute("activePromo", activePromo);
            request.setAttribute("discountedPrice", discountedPrice);
        }

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        request.setAttribute("roomType", roomType);
        request.setAttribute("availableCount", availableCount);
        request.setAttribute("availableRooms", availableRooms);

        request.getRequestDispatcher("/WEB-INF/views/room/detail.jsp").forward(request, response);
    }

    private Integer parseIntParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
