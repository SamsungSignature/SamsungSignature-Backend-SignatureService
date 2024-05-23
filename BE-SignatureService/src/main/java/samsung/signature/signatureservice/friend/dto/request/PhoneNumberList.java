package samsung.signature.signatureservice.friend.dto.request;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public record PhoneNumberList(
	@JsonProperty("phone_number_list")
	List<FriendInfos> friendInfos
) {
	@Getter
	public static class FriendInfos {
		@JsonProperty("displayName")
		private String displayName;

		@JsonProperty("phoneNumber")
		private String phoneNumber;

		@JsonProperty("thumbnailPath")
		private String thumbnailPath;

		private String formatPhoneNumber(String phoneNumber) {
			String cleaned = phoneNumber.replaceAll("\\D", "");

			Pattern pattern = Pattern.compile("(\\d{3})(\\d{4})(\\d{4})");
			Matcher matcher = pattern.matcher(cleaned);

			return matcher.replaceAll("$1-$2-$3");

		}

		public void setPhoneNumber(String phoneNumber) {
			this.phoneNumber = formatPhoneNumber(phoneNumber);
		}
	}
}

