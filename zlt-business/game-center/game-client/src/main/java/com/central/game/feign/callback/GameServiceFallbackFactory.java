package com.central.game.feign.callback;

import com.central.common.model.Result;
import com.central.game.feign.GameService;
import com.central.game.model.GameList;
import com.central.game.model.GameRoomList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

/**
 * 降级工场
 */
@Slf4j
public class GameServiceFallbackFactory implements FallbackFactory<GameService> {

    @Override
    public GameService create(Throwable throwable) {
        return new GameService() {
            @Override
            public Result save(GameList gameList) {
                log.error("游戏保存失败:{}", gameList, throwable);
                return Result.failed("游戏保存失败");
            }

            @Override
            public Result<List<GameList>> findAll() {
                log.error("查询全部游戏失败", throwable);
                return Result.failed("查询全部游戏失败");
            }

            @Override
            public Result<GameList> findById(Long id) {
                log.error("根据ID查询游戏详情失败", throwable);
                return Result.failed("根据ID查询游戏详情失败");
            }

            @Override
            public Result deleteById(Long id) {
                log.error("根据ID删除游戏失败", throwable);
                return Result.failed("根据ID删除游戏失败");
            }

            @Override
            public Result<List<GameList>> findAllOpenRate() {
                log.error("查询全部开启返水的游戏失败", throwable);
                return Result.failed("查询全部开启返水的游戏失败");
            }

            @Override
            public Result<List<GameList>> findGameList(Integer state) {
                log.error("findGameList查询洗码配置列表失败", throwable);
                return Result.failed("查询失败");
            }

            @Override
            public Result<List<GameRoomList>> findList(Long gameId) {
                log.error("findList查询房间列表失败", throwable);
                return Result.failed("查询失败");
            }

            @Override
            public Result save(GameRoomList gameRoomList) {
                log.error("save房间失败", throwable);
                return Result.failed("保存房间失败");
            }

            @Override
            public Result updateRoomStatus(Long id, Integer roomStatus, String maintainStart, String maintainEnd) {
                log.error("更新房间失败", throwable);
                return Result.failed("更新房间失败");
            }
            @Override
            public Result roomDeleteById(Long id) {
                log.error("删除房间失败", throwable);
                return Result.failed("删除房间失败");
            }

            @Override
            public Result<GameRoomList> findRoomDetailById(Long id) {
                log.error("findRoomDetailById查询房间详情失败id:{}", id);
                return Result.failed("查询房间详情失败");
            }

            @Override
            public Result<List<GameRoomList>> findRoomDetailByIds(String ids) {
                log.error("findRoomDetailByIds查询房间详情失败ids:{}", ids);
                return Result.failed("查询房间详情失败");
            }

        };
    }
}
