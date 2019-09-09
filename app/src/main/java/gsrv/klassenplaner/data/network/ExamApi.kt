package gsrv.klassenplaner.data.network

import gsrv.klassenplaner.data.entities.Exam
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

// TODO: Change paths to reflect actual endpoints
interface ExamApi {
    @GET("groups/{group_id}/exams")
    suspend fun getExams(
        @Path("group_id") groupId: Int
    ): List<Exam>

    @POST("groups/{group_id}/exams")
    suspend fun createExam(
        @Path("group_id") groupId: Int,
        @Body exam: Exam
    ): Exam

    @DELETE("exams/{exam_id}")
    suspend fun deleteExam(
        /*@Path("group_id") groupId: Int,*/
        @Path("exam_id") examId: Int
    )

    @PATCH("exams/{exam_id}")
    suspend fun updateExam(
        /*@Path("group_id") groupId: Int,*/
        @Path("exam_id") examId: Int,
        @Body exam: Exam
    ): Exam
}
