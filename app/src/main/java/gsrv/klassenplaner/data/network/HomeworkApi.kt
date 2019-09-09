package gsrv.klassenplaner.data.network

import gsrv.klassenplaner.data.entities.Homework
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

// TODO: Change paths to reflect actual endpoints
interface HomeworkApi {
    @GET("groups/{group_id}/homework")
    suspend fun getHomework(
        @Path("group_id") groupId: Int
    ): List<Homework>

    @POST("groups/{group_id}/homework")
    suspend fun createHomework(
        @Path("group_id") groupId: Int,
        @Body homework: Homework
    ): Homework

    @DELETE("homework/{homework_id}")
    suspend fun deleteHomework(
        /*@Path("group_id") groupId: Int,*/
        @Path("homework_id") homeworkId: Int
    )

    @PATCH("homework/{homework_id}")
    suspend fun updateHomework(
        /*@Path("group_id") groupId: Int,*/
        @Path("homework_id") homeworkId: Int,
        @Body homework: Homework
    ): Homework
}
