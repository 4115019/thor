import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author huangpin
 * @date 2019-12-27
 */
public class D20191227L1V2 {
    public int[] twoSum(int[] nums, int target) {
        int[] result = new int[2];
        Map<Integer, Integer> numMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < nums.length; i++) {
            numMap.put(nums[i], i);
        }

        for (int i = 0; i < nums.length; i++) {
            Integer index = numMap.get(target - nums[i]);
            if (index == null
                    || index == i) {
                continue;
            }

            if (index > i) {
                result[0] = i;
                result[1] = index;
            } else {
                result[0] = index;
                result[1] = i;
            }
            return result;
        }
        return null;
    }
}
