package gsrv.klassenplaner.data.network

import gsrv.klassenplaner.data.entities.Group
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GroupApi {
    @GET("groups/{group_id}")
    suspend fun getGroup(
        @Path("group_id") groupId: Int
    ): Group

    @POST("groups")
    suspend fun createGroup(
        @Body group: Group
    ): Group

    @DELETE("groups/{group_id}")
    suspend fun deleteGroup(
        @Path("group_id") groupId: Int
    )

    @PATCH("groups/{group_id}")
    suspend fun updateGroup(
        @Path("group_id") groupId: Int,
        @Body group: Group
    ): Group
}
