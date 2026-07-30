package an.ph69924.bansach.api;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import an.ph69924.bansach.models.ApiResponse;
import an.ph69924.bansach.models.Book;
import an.ph69924.bansach.models.BooksResponse;
import an.ph69924.bansach.models.CartResponse;
import an.ph69924.bansach.models.CategoriesResponse;
import an.ph69924.bansach.models.Order;
import an.ph69924.bansach.models.OrdersResponse;
import an.ph69924.bansach.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    @POST("api/auth/login")
    Call<ApiResponse<User>> login(@Body User user);

    @POST("api/auth/register")
    Call<ApiResponse<User>> register(@Body User user);

    @GET("api/auth/profile")
    Call<ApiResponse<User>> getProfile(@Header("Authorization") String token);

    @GET("api/books")
    Call<BooksResponse> getBooks(
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("category") String category,
            @Query("search") String search
    );

    @GET("api/books/{id}")
    Call<ApiResponse<Book>> getBookDetail(@Path("id") String id);

    @GET("api/categories")
    Call<CategoriesResponse> getCategories();

    @GET("api/cart")
    Call<ApiResponse<CartResponse>> getCart(@Header("Authorization") String token);

    @POST("api/cart")
    Call<ApiResponse<CartResponse>> addToCart(@Header("Authorization") String token, @Body Map<String, Object> body);

    @PUT("api/cart")
    Call<ApiResponse<CartResponse>> updateCartItem(@Header("Authorization") String token, @Body Map<String, Object> body);

    @retrofit2.http.DELETE("api/cart/{bookId}")
    Call<ApiResponse<CartResponse>> removeFromCart(@Header("Authorization") String token, @Path("bookId") String bookId);

    @POST
    Call<ResponseBody> postCartDynamic(@Header("Authorization") String token, @Url String url, @Body Map<String, Object> body);

    @POST("api/orders")
    Call<ApiResponse<Order>> createOrder(@Header("Authorization") String token, @Body Map<String, Object> body);

    @GET("api/orders")
    Call<ApiResponse<OrdersResponse>> getOrders(@Header("Authorization") String token);

    @GET("api/orders/{id}")
    Call<ApiResponse<Order>> getOrderDetail(@Header("Authorization") String token, @Path("id") String id);

    @Multipart
    @POST("api/users/avatar")
    Call<ApiResponse<User>> uploadAvatar(@Header("Authorization") String token, @Part MultipartBody.Part image);

    @Multipart
    @POST("api/users/avatar")
    Call<ResponseBody> uploadAvatarApiRaw(@Header("Authorization") String token, @Part MultipartBody.Part image);
}
